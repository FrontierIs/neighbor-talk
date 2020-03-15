const {ipcRenderer} = require('electron')
// 同期
console.log("preloadtest")
// 非同期
var items
var suggest
var pref={}
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

function compare(a,b){
    return preflist[a]-preflist[b];
}

function rankcompare(a,b){
  return rank[a]-rank[b];
}

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


function getCsv(url){
  //CSVファイルを文字列で取得。
  var txt = new XMLHttpRequest();
  txt.open('get', url, false);
  txt.send();

  //改行ごとに配列化
  var arr = txt.responseText.split('\n');

  //1次元配列を2次元配列に変換
  var res = [];
  for(var i = 0; i < arr.length; i++){
    //空白行が出てきた時点で終了
    if(arr[i] == '') break;

    //","ごとに配列化
    res[i] = arr[i].split(',');

    for(var i2 = 0; i2 < res[i].length; i2++){
      //数字の場合は「"」を削除
      if(res[i][i2].match(/\-?\d+(.\d+)?(e[\+\-]d+)?/)){
        res[i][i2] = parseFloat(res[i][i2].replace('"', ''));
      }
    }
  }

  return res;
}

function dot(matrix1, matrix2){
  var res = [];
  var row1 = matrix1.length;
  var row2 = matrix2.length;
  var col1 = matrix1[0].length;
  var col2 = matrix2[0].length;

  for(var i1 = 0; i1 < row1; i1++){
    res.push([]);
    for(var i2 = 0; i2 < col2; i2++){
      res[i1].push(0);
      for(var i3 = 0; i3 < col1; i3++){
        res[i1][i2] += matrix1[i1][i3] * matrix2[i3][i2];
      }
    }
  }

  return res;
}

//console.log(items)

var talktitle=["sh;along\n","sh;anega_long\n","sh;agold\n","sh;asilver\n","sh;arubi\n","sh;aparu\n","sh;ashizuku\n","sh;aheart\n"]

var i=1
var tsunagi=function(url){
  if (url.indexOf("item=13") != -1) {
    console.log("sotaしゃべる")
    i=Math.floor(Math.random() * (talktitle.length - 0))
    ipcRenderer.sendToHost('hover',"sh;clear\n")
    ipcRenderer.sendToHost('hover',talktitle[i])
  }
}

var url=[]
var can=""
var hov=null;
var finalcan=null;
var cd
var doc = window.document;
var ele=null
// window.addEventListener("load", function(){
var start
var timer
var now_url=""
//var number=["zero","one","two","three","four","five","six","seven","eight","nine","ten"]
var p=""
var long=["sh;long\n","sh;long2\n"]
var paru=["sh;paru\n","sh;paru2\n","sh;paru3\n"]
var shizuku=["sh;shizuku\n","sh;shizuku2\n"]
var shy=["sh;shy\n","sh;dis\n","sh;child\n"]
var firstkey=["長い","短い","ハート","しずく","白い","赤い","青い","ゴールド","シルバー"]
var recolist={}
var negalist=[]
//var show=1

//sessionStorage.setItem("notsug",notsug)
window.onload = function(){
  not=sessionStorage.getItem("notsug")
  notsug=new Array()
  if(not!=null){
    notsug=not.split(",")
  }
  console.log(typeof notsug)
  now_url=window.location.href;
  items=sessionStorage.getItem('items');
  items=JSON.parse(items)
  preflist=sessionStorage.getItem("pref")
  preflist=JSON.parse(preflist)
  suggest=Number(sessionStorage.getItem("suggest"))
  console.log(items)
  console.log("今のURLは")
  console.log(now_url)



  //初期ページへ来たら
  if ( now_url.indexOf("https://jewelry-boutique.jp/products?item=13")!= -1){
    images=document.getElementsByClassName("itemInfo_sn")
    console.dir(images);
    //HTML書き換えて、商品それぞれに番号をつける
    for(let i=0;i<images.length;i++){
        let no=Number(i)+1
        //console.dir(images[i]);
        atag=images[i].getElementsByTagName("a")

        itemno=atag[0].href.match(/[0-9]+\.?[0-9]*/g);
        //console.log(itemno)
        recolist[itemno[0]]=no
        images[i].insertAdjacentHTML('afterbegin','<p style="font-weight:bold; font-size:24px;">'+no+"</p>");
    }
    console.dir(recolist)
    if (suggest==0){
      console.log("random talk start")
      start=setTimeout(function(){
                p=sessionStorage.getItem('start')
                if (p==null){
                  ipcRenderer.sendToHost('hover',"sh;a4doc\n")
                  sessionStorage.setItem("start",1)
                  timer=setInterval(tsunagi,35000,now_url)
                }
                else{
                  i=Math.floor(Math.random() * (talktitle.length - 0))
                  ipcRenderer.sendToHost('hover',talktitle[i])
                  //最初はnarrowのシナリオ、その後はインターバルでランダムなシナリオ
                  timer=setInterval(tsunagi,35000,now_url)
                }
              },6000)
    }
    else{
      console.log("start suggest")
      //rank上位から推薦する、推薦の条件　notsugにないやつ
      keys=sessionStorage.getItem('keys')
      keys=JSON.parse(keys)
      console.log(keys)
      //if(recono==null){
      recono=sessionStorage.getItem("recono")
      talkcount=sessionStorage.getItem("talkcount")
        for(i=0;i<keys.length;i++){
          if (notsug.indexOf(keys[keys.length-i]) == -1 && typeof recolist[keys[keys.length-i]] !== "undefined"){
          //  talkcount=sessionStorage.getItem("talkcount")
            //  sessionStorage["talkcount"]=talkcount+1

          if(recono==null || talkcount==0){
              //sessionStorage["recono"]=recolist[keys[keys.length-i]]
              string="sh;a"+recolist[keys[keys.length-i]]+"\n"
              if (recolist[keys[keys.length-i]]==1){
                 start=setTimeout(function(){
                  ipcRenderer.sendToHost('hover',"sh;clear\n")
                  ipcRenderer.sendToHost('hover',"sh;a1.0\n")
                  console.log("1.0送った")
                 },2500)
              }

              else if (recolist[keys[keys.length-i]]==2){
                start=setTimeout(function(){
                 ipcRenderer.sendToHost('hover',"sh;clear\n")
                 ipcRenderer.sendToHost('hover',"sh;a2.0\n")
                 console.log("2.0送った")
                },2500)
              }

              else if (recolist[keys[keys.length-i]]==3){
                start=setTimeout(function(){
                 ipcRenderer.sendToHost('hover',"sh;clear\n")
                 ipcRenderer.sendToHost('hover',"sh;a3.0\n")
                 console.log("3.0送った")
                },2500)
              }




              console.log(recolist[keys[keys.length-i]])
              notsug.push(keys[keys.length-i])
              sessionStorage.setItem("notsug",notsug.toString())
              start=setTimeout(function(){
                console.log("ifbun")
                ipcRenderer.sendToHost('hover',"sh;clear\n")
                ipcRenderer.sendToHost('hover',string)
              },7000)
              sessionStorage["recono"]=recolist[keys[keys.length-i]]
              sessionStorage["talkcount"]=1
              break;
          }
          else if(talkcount==1){
            string="sh;a"+recono+"_2"+"\n"
            start=setTimeout(function(){
              console.log(string)
              ipcRenderer.sendToHost('hover',"sh;clear\n")
              ipcRenderer.sendToHost('hover',string)
            },7000)
            sessionStorage["talkcount"]=2
            break;
          }
          else if(talkcount==2){
            string="sh;a"+recono+"_3\n"
            start=setTimeout(function(){
              console.log(string)
              ipcRenderer.sendToHost('hover',"sh;clear\n")
              ipcRenderer.sendToHost('hover',string)
            },7000)
            sessionStorage["talkcount"]=0
            break;
          }
        }

      }
    }
  }
  //初期ページじゃないなら
  else {
  //counterの値、取得して+1して保存
    counter=Number(sessionStorage.getItem("counter"))
    console.log("counter"+ counter)
    sessionStorage.setItem("counter",counter+1)

    console.log(window.History.length)
    var array = now_url.match(/[0-9]+\.?[0-9]*/g);
    //  console.log(array[0]);
    notsug.push(array[0])
    console.dir(notsug)
    sessionStorage.setItem("notsug",notsug.toString())
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

                                  //詳細ページのテキスト取ってくるやつ
                                  //divs=document.getElementsByClassName("products_description cb")
                                  //ptag=divs[0].getElementsByTagName("p")
                                  //console.dir(ptag[1].innerText)

    //preflistのmax-minが閾値こえたら
    // 商品紹介モード(suggest)
   if (preflist[keys[keys.length-1]]-preflist[keys[0]]>=3 || counter>=4 ){


      suggest=1
      sessionStorage["suggest"]=suggest
      matrix2=[]
      for (i=0;i<firstkey.length;i++){
        //console.log("matrix.push")
        matrix2.push([preflist[firstkey[i]]])
      }
      //item行列を計算用に格納
      matrix1=items.slice(1,items.length)

      for(i=0;i<matrix1.length;i++){
        temp[i]=matrix1[i].shift()
      }
      result=dot(matrix1,matrix2)
      console.log(temp.length)
      //resultと商品Noを紐づけしてrank作成
      rank={}
      for(i=0;i<result.length;i++){
          rank[temp[i]]=result[i]
      }
    //  console.log(rank.length)
      keys= Object.keys(rank)
      //console.log(keys)
      keys.sort(rankcompare)

      negalist=keys.slice(0,10)
      //console.log(keys.slice(0,9))
      sessionStorage.setItem('keys', JSON.stringify(keys))

      console.log(negalist)
      if (negalist.indexOf(array[0]) == -1){
        console.log(items)
                if (counter==4){
                  start=setTimeout(function(){
                    ipcRenderer.sendToHost('hover',"sh;clear\n")
                    ipcRenderer.sendToHost('hover',"sh;alist2\n")
                    console.log("list送った")
                  },2500)
                }

                else if (items[show][2]==1){
                  start=setTimeout(function(){
                    ipcRenderer.sendToHost('hover',"sh;clear\n")
                    ipcRenderer.sendToHost('hover',"sh;aok_heart\n")
                    console.log("ハート送った")
                    setTimeout(function(){
                      ipcRenderer.sendToHost("hover","sh;aboost\n")
                    },35000)
                  },2500)

                }
                else if (items[show][3]==1){
                  start=setTimeout(function(){
                    ipcRenderer.sendToHost('hover',"sh;clear\n")
                    ipcRenderer.sendToHost('hover',"sh;aok_shizuku\n")
                    console.log("しずく送った")
                    setTimeout(function(){
                      ipcRenderer.sendToHost("hover","sh;aboost\n")
                    },35000)
                  },2500)
                }

                else if(items[show][8]==1){
                  start=setTimeout(function(){
                    ipcRenderer.sendToHost('hover',"sh;clear\n")
                    ipcRenderer.sendToHost('hover',"sh;aok_silver\n")
                    console.log("silver送った")
                    setTimeout(function(){
                      ipcRenderer.sendToHost("hover","sh;aboost\n")
                    },35000)
                    },2500)
                }

                else if (items[show][7]==1){
                  start=setTimeout(function(){
                    ipcRenderer.sendToHost('hover',"sh;clear\n")
                    ipcRenderer.sendToHost('hover',"sh;aok_gold\n")
                    console.log("gold送った")
                    setTimeout(function(){
                      ipcRenderer.sendToHost("hover","sh;aboost\n")
                    },35000)
                  },2500)
                }

                else if (items[show][0]==1){
                  start=setTimeout(function(){
                    ipcRenderer.sendToHost('hover',"sh;clear\n")
                    ipcRenderer.sendToHost('hover',"sh;aok_long\n")
                    console.log("long送った")
                    setTimeout(function(){
                      ipcRenderer.sendToHost("hover","sh;aboost\n")
                    },35000)
                  },2500)
                }

                else{
                          setTimeout(function(){
                            ipcRenderer.sendToHost('hover',"sh;clear\n")
                            ipcRenderer.sendToHost('hover', "sh;agood\n")
                            setTimeout(function(){
                              ipcRenderer.sendToHost("hover","sh;aboost\n")
                            },35000)
                          },2000)
              }
      }

      else{
        setTimeout(function(){
          ipcRenderer.sendToHost('hover',"sh;clear\n")
          ipcRenderer.sendToHost('hover', "sh;adis\n")
        },2000)
      }


    }
    //
    //suggestモードではないとき
    else{
      maxinde=1
      let value = -Infinity
      for  (i = 1;i<items[show].length; i++) {
  		  if (value <= items[show][i]) {
  			     value = items[show][i]
  			     maxinde = i
        }
  		}
      console.log("maxinde"+maxinde)
      if (counter==3){
        start=setTimeout(function(){
          ipcRenderer.sendToHost('hover',"sh;clear\n")
          ipcRenderer.sendToHost('hover',"sh;alist1\n")
          console.log("list送った")
        },2500)
      }
      else if (items[show][3]==1){
        start=setTimeout(function(){
          ipcRenderer.sendToHost('hover',"sh;clear\n")
          ipcRenderer.sendToHost('hover',"sh;aka_heart\n")
          console.log("ハート送った")
        },2500)

      }
      else if (items[show][4]==1){
        start=setTimeout(function(){
          ipcRenderer.sendToHost('hover',"sh;clear\n")
          ipcRenderer.sendToHost('hover',"sh;aka_shizuku\n")
          console.log("しずく送った")
        },2500)
      }

      else{
          start=setTimeout(function(){
            ipcRenderer.sendToHost('hover',"sh;clear\n")
            ipcRenderer.sendToHost('hover',"sh;akanren\n")
            console.log("kanren送った")
          },2500)
      }


    }
 }


  window.onmousemove = function(){
               doc.querySelectorAll(':hover').forEach( item =>
               item.querySelectorAll(':hover').forEach( item =>
               item.tagName == "A" ? ele = item : {}))

               // console.log(ele);
             // var ele = doc.querySelector(":hover");
             can=ele.href;
             if(hov == null){
                 hov=ele.href;
                 hoge=setTimeout(function(){
                   if(hov == can){
                     finalcan=hov;
                   }
                 },2500)
             }

             if(can != hov)
             {
               clearTimeout(hoge);
               console.log('リセット');
               hov=null;
             }

             if(finalcan == can){
               console.log('hover recognize');
               // ipcRenderer.sendToHost('hover',url)
               cd = finalcan;

               // postData = {"URL":cd,"pagepointar":flag,"abc":minitalkpointar};
               console.log(cd);
               // $.post("/listen",postData);
               finalcan=null;
             }
           };
}
