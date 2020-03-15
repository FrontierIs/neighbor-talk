const {ipcRenderer} = require('electron')
// 同期
console.log("preloadtest")
// 非同期
var items
var suggest
var pref={} //ユーザの好みを記憶する辞書配列　このプログラムでは使われてない
pref["長い"]=0
pref["短い"]=0
pref["ハート"]=0
pref["しずく"]=0
pref["白い"]=0
pref["赤い"]=0
pref["青い"]=0
pref["ゴールド"]=0
pref["シルバー"]=0

var keys= Object.keys(pref);
var temp=[]

function compare(a,b){ //辞書配列をソートする関数
    return preflist[a]-preflist[b];
}

function rankcompare(a,b){
  return rank[a]-rank[b];
}

//rendere.jsからアイテムの特徴量を受け取って、ローカルストレージに格納
ipcRenderer.on('from_renderer', (event, arg) => {
  items=arg // pong
//  items[0]=["No","長い","短い","ハート","しずく","白い","赤い","青い","ゴールド","シルバー"]
  //console.log(arg)
  //console.log(items)
  sessionStorage.setItem('items', JSON.stringify(items))
  //console.log("set item&pref")
  sessionStorage.setItem("pref",JSON.stringify(pref))
  // ipcRenderer.sendToHost('hover', 'preload')
})



// var talktitle=["sh;long\n","sh;gold\n","sh;silver\n","sh;rubi\n","sh;paru\n","sh;shizuku\n","sh;heart\n"]

var i=1

var url=[]
var can=""
var hov=null;
var finalcan=null;
var cd
var doc = window.document;
var ele=null
var start
var timer
var now_url=""

var p=""
var long=["sh;long\n","sh;long2\n"]
var paru=["sh;paru\n","sh;paru2\n","sh;paru3\n"]
var shizuku=["sh;shizuku\n","sh;shizuku2\n"]
var shy=["sh;shy\n","sh;dis\n","sh;child\n"]
var firstkey=["長い","短い","ハート","しずく","白い","赤い","青い","ゴールド","シルバー"]
var recolist={}
var negalist=[]
var recotalk=["1.0","24","25"]
//var show=1

//sessionStorage.setItem("notsug",notsug)
window.onload = function(){ //ページが読み込まれたら以下を実行
  not=sessionStorage.getItem("notsug")
  notsug=new Array()
  if(not!=null){
    notsug=not.split(",")
  }
  // console.log(typeof notsug)
  now_url=window.location.href;
  items=sessionStorage.getItem('items');
  items=JSON.parse(items)
  preflist=sessionStorage.getItem("pref")
  preflist=JSON.parse(preflist)
  suggest=Number(sessionStorage.getItem("suggest"))//現在suggestモードであるかどうかを示す
  console.log(items)
  console.log("今のURLは")
  console.log(now_url)



  //一覧ページへ来たら
  if ( now_url.indexOf("https://jewelry-boutique.jp/products?item=13")!= -1){
  //4℃サイトのHTML書き換えて、商品それぞれに番号をつける
    images=document.getElementsByClassName("itemInfo_sn")
    console.dir(images);

    if (items==null){
      console.log("items==null")
      for(let i=0;i<images.length;i++){
          let no=Number(i)+1
          //console.dir(images[i]);
          atag=images[i].getElementsByTagName("a")

          itemno=atag[0].href.match(/[0-9]+\.?[0-9]*/g);
          //console.log(itemno)
          //recolist[itemno[0]]=no
          images[i].insertAdjacentHTML('afterbegin','<p style="font-weight:bold; font-size:24px;">'+no+"</p>");
      }
    }
    else{
      for(let i=0;i<images.length;i++){
        //let no=Number(i)+1
        //console.dir(images[i]);
        atag=images[i].getElementsByTagName("a")

        itemno=atag[0].href.match(/[0-9]+\.?[0-9]*/g);
        //console.log("itemno"+itemno)
        for(let j=0;j<55;j++){
          if (items[j][0]==itemno){
          //  console.log("in if")
            images[i].insertAdjacentHTML('afterbegin','<p style="font-weight:bold; font-size:24px;">'+items[j][10]+"</p>");
            break
          }
        }

    }
    }
    //一覧ページ　suggestじゃないとき
    if (suggest==0){
      console.log("random talk start")
      //訪問回数に応じて話すシナリオを決定
      start=setTimeout(function(){
                visit_number=Number(sessionStorage.getItem('start'))//訪問回数を変数visitnumberに格納
                console.log("p"+p)
                if (visit_number==null || visit_number==0){
                  ipcRenderer.sendToHost('hover',"sh;4doc\n")
                  sessionStorage.setItem("start",1)
                  //timer=setInterval(tsunagi,35000,now_url)
                  sessionStorage["start"]=1
                }
                else if(visit_number==1){
                  //i=Math.floor(Math.random() * (talktitle.length - 0))
                  ipcRenderer.sendToHost('hover',"sh;clear\n")
                  ipcRenderer.sendToHost('hover',"sh;cool\n")
                  //最初はnarrowのシナリオ、その後はインターバルでランダムなシナリオ
                  //timer=setInterval(tsunagi,35000,now_url)
                  sessionStorage["start"]=2
                }
                else{
                  ipcRenderer.sendToHost('hover',"sh;clear\n")
                  ipcRenderer.sendToHost('hover',"sh;first\n")
                }
              },6000)
    }
    else{ //一覧ページでsuggestモードなら
      console.log("start suggest")
      //rank上位から推薦する、推薦の条件　notsugにないやつ
      keys=sessionStorage.getItem('keys')
      keys=JSON.parse(keys)
      console.log(keys)
      //if(recono==null){
      recono=sessionStorage.getItem("recono")
      talkcount=sessionStorage.getItem("talkcount")
      if(recono==null || talkcount==0){　//推薦する商品が決まっていないなら
          i=Math.floor(Math.random() * (recotalk.length - 0)) //推薦する商品をランダムで決定
          string="sh;"+recotalk[i]+"\n"  //blackboardに送る文字列を作成
          start=setTimeout(function(){
            console.log(string)
            ipcRenderer.sendToHost('hover',"sh;clear\n") //前に行われている動作を削除する命令をrenderer.jsに送信　
            ipcRenderer.sendToHost('hover',string) //推薦する商品のシナリオをrenderer.jsに送信　
          },3500)
          sessionStorage["recono"]=recotalk[i]
          if (i==0){
            sessionStorage["recono"]="1" //推薦する商品番号をローカルストレージに格納
          }
          sessionStorage["talkcount"]=1 //商品を推薦した回数をローカルストレージに格納
      }

      else if(talkcount==1){ //推薦回数が1回なら
        string="sh;"+recono+"_2"+"\n"
        start=setTimeout(function(){
          console.log(string)
          ipcRenderer.sendToHost('hover',"sh;clear\n")
          ipcRenderer.sendToHost('hover',string)
        },7000)
        sessionStorage["talkcount"]=2
        //break;
      }
      else if(talkcount==2){ //推薦回数が2回なら
        string="sh;"+recono+"_3\n"
        start=setTimeout(function(){
          console.log(string)
          ipcRenderer.sendToHost('hover',"sh;clear\n")
          ipcRenderer.sendToHost('hover',string)
        },7000)
        sessionStorage["talkcount"]=0
        //break;
      }

   }
}
  //詳細ページ
  else {
  //counterの値、取得して+1して保存
    counter=Number(sessionStorage.getItem("counter"))
    console.log("counter"+ counter)
    sessionStorage.setItem("counter",counter+1)

    console.log(window.History.length)
    var array = now_url.match(/[0-9]+\.?[0-9]*/g);
    //  console.log(array[0]);
    //preflistにアイテムの値追加
    for (i=0;i<items.length;i++){
      //console.log(items[i][0])
     if (items[i][0]==array[0]){

       show=i
       console.log("if"+show)
       preflist["長い"]+=items[i][1]
       preflist["短い"]+=items[i][2]
       preflist["ハート"]+=items[i][3]
       preflist["しずく"]+=items[i][4]
       preflist["白い"]+=items[i][5]
       preflist["赤い"]+=items[i][6]
       preflist["青い"]+=items[i][7]
       preflist["ゴールド"]+=items[i][8]
       preflist["シルバー"]+=items[i][9]

     }
   }

    sessionStorage.setItem("pref",JSON.stringify(preflist))
    console.log(preflist)
    //preflistをキーでソート
    keys.sort(compare)
    console.log(keys)



    //preflistのmax-minが閾値こえたら
    // 商品紹介モード(suggest)
   if (preflist[keys[keys.length-1]]-preflist[keys[0]]>=3 || counter>=3 ){


      suggest=1
      sessionStorage["suggest"]=suggest
  }

        console.log(items)


      console.log(items[show])
      // if (counter==3){
      //   start=setTimeout(function(){
      //     ipcRenderer.sendToHost('hover',"sh;clear\n")
      //     ipcRenderer.sendToHost('hover',"sh;list1\n")
      //     console.log("list送った")
      //   },2500)
      // }

      if(items[show][9]==1 && items[show][1]==1){
        start=setTimeout(function(){
          ipcRenderer.sendToHost('hover',"sh;clear\n")
          ipcRenderer.sendToHost('hover',"sh;target\n")
          console.log("silver送った")
          setTimeout(function(){
            ipcRenderer.sendToHost("hover","sh;boost\n")
          },40000)
          },2500)
      }

      else if(items[show][9]==1){
        start=setTimeout(function(){
          ipcRenderer.sendToHost('hover',"sh;clear\n")
          ipcRenderer.sendToHost('hover',"sh;ok_silver\n")
          console.log("silver送った")
          setTimeout(function(){
            ipcRenderer.sendToHost("hover","sh;boost\n")
          },45000)
          },2500)
      }

      else if(items[show][1]==1){
        start=setTimeout(function(){
          ipcRenderer.sendToHost('hover',"sh;clear\n")
          ipcRenderer.sendToHost('hover',"sh;ok_long\n")
          console.log("silver送った")
          setTimeout(function(){
            ipcRenderer.sendToHost("hover","sh;boost\n")
          },45000)
          },2500)
      }

      else if (items[show][3]==1){
        start=setTimeout(function(){
          ipcRenderer.sendToHost('hover',"sh;clear\n")
          ipcRenderer.sendToHost('hover',"sh;ka_heart\n")
          console.log("ハート送った")
        },2500)

      }
      else if (items[show][4]==1){
        start=setTimeout(function(){
          ipcRenderer.sendToHost('hover',"sh;clear\n")
          ipcRenderer.sendToHost('hover',"sh;ka_shizuku\n")
          console.log("しずく送った")
        },2500)
      }

      else{
          start=setTimeout(function(){
            ipcRenderer.sendToHost('hover',"sh;clear\n")
            ipcRenderer.sendToHost('hover',"sh;kanren\n")
            console.log("kanren送った")
          },2500)
      }



 }

}
