const {ipcRenderer} = require('electron')
// 同期
console.log("preloadtest")
// 非同期
ipcRenderer.on('from_renderer', (event, arg) => {
  console.log(arg) // pong
  // ipcRenderer.sendToHost('hover', 'preload')
})
var talktitle=["sh;narrow-heart\n","sh;nega-long\n","sh;narrow\n"]
var i=1
var tsunagi=function(url){
  if (url.indexOf("item=13") != -1) {
    console.log("sotaしゃべる")
    i=Math.floor(Math.random() * (talktitle.length - 0))
    ipcRenderer.sendToHost('hover',"sh;clear\n")
    ipcRenderer.sendToHost('hover',talktitle[i])

    // if (i == talktitle.length-1){
    //   i=0
    // }
  }
}


//
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
var number=["zero","one","two","three","four","five","six","seven","eight","nine","ten"]
var p=""
var cute=["sh;cute\n","sh;simple\n","sh;hade\n","sh;good\n"]
var long=["sh;long\n","sh;long2\n"]
var paru=["sh;paru\n","sh;paru2\n","sh;paru3\n"]
var shizuku=["sh;shizuku\n","sh;shizuku2\n"]
var shy=["sh;shy\n","sh;dis\n","sh;child\n"]
window.onload = function(){

  now_url=window.location.href;
  console.log(url)
  console.log("今のURLは")
  console.log(now_url)

  //初期ページへ来たら
  if ( now_url.indexOf("https://jewelry-boutique.jp/products?item=13")!= -1){
    //2回目以降なら
    p=sessionStorage.getItem('url')
    console.log(p)
    if ( p == null ){

        console.log("初めての訪問です")

        //url.push(now_url)
        sessionStorage.setItem('url', now_url);
        start=setTimeout(function(){
                  ipcRenderer.sendToHost('hover',"sh;narrow-gold\n")
                  //最初はnarrowのシナリオ、その後はインターバルでランダムなシナリオ
                  timer=setInterval(tsunagi,40000,now_url)
                    },10000)
    }
    else {

        console.log("戻ってきました")

        start=setTimeout(function(){
                          //最初はnarrowのシナリオ、その後はインターバルでランダムなシナリオ
                          timer=setInterval(tsunagi,25000,now_url)
                        },5000)

    }


  }
  //初期ページじゃないなら
  else {
    console.log(window.History.length)


  if (now_url.indexOf("151736153107") != -1 || now_url.indexOf("151836153119") != -1 || now_url.indexOf("151834151017") != -1 || now_url.indexOf("151646153220") != -1 ||
              now_url.indexOf("151736153205") != -1 || now_url.indexOf("151646153135") != -1 || now_url.indexOf("151646153132") != -1 ||
            now_url.indexOf ("151646153217") != -1 || now_url.indexOf ("151646153216") != -1  ){
        i=Math.floor(Math.random() * (cute.length - 0))
        setTimeout(function(){
          ipcRenderer.sendToHost('hover',"sh;clear\n")
          ipcRenderer.sendToHost('hover', cute[i])
        },2000)

   }

   else  if (now_url.indexOf("151926153201") != -1 || now_url.indexOf("151836153008") != -1 || now_url.indexOf("151926153101") != -1 || now_url.indexOf("111846153001") != -1 ||
             now_url.indexOf("111816153002") != -1 || now_url.indexOf("111846153006") != -1 || now_url.indexOf("111746153106") != -1 ){
               p=sessionStorage.getItem('paru')
               // 初回訪問時
               if (p==null){
               setTimeout(function(){
                   sessionStorage.setItem('paru', 1);
                   ipcRenderer.sendToHost('hover',"sh;clear\n")
                   ipcRenderer.sendToHost('hover', paru[0])
                 },2000)
               }

               // 2回目以降訪問時
               else{
                 setTimeout(function(){
                     ipcRenderer.sendToHost('hover',"sh;clear\n")
                     ipcRenderer.sendToHost('hover', paru[1])
                 },2000)
               }
   }

   else if(now_url.indexOf("151836153213") != -1 || now_url.indexOf("151836153121") != -1 || now_url.indexOf("151834151020") != -1 || now_url.indexOf("151736153112") != -1 ||
             now_url.indexOf("111846153006") != -1 || now_url.indexOf("111746153213") != -1 || now_url.indexOf("111746153010") != -1 ){
               p=sessionStorage.getItem('shizuku')
               // 初回訪問時
               if (p==null){
               setTimeout(function(){
                   sessionStorage.setItem('shizuku', 1);
                   ipcRenderer.sendToHost('hover',"sh;clear\n")
                   ipcRenderer.sendToHost('hover', shizuku[0])
                 },2000)
               }

               // 2回目以降訪問時
               else{
                 setTimeout(function(){
                     ipcRenderer.sendToHost('hover',"sh;clear\n")
                     ipcRenderer.sendToHost('hover', shizuku[1])
                 },2000)
               }
   }
   else if(now_url.indexOf("151646153219") != -1 || now_url.indexOf("111746153215") != -1 || now_url.indexOf("151834151036") != -1 || now_url.indexOf("151834151015") != -1 ||
             now_url.indexOf("151646153134") != -1 || now_url.indexOf("111746153213") != -1 || now_url.indexOf("111746153010") != -1 || now_url.indexOf ("111746153212") != -1 ){
               p=sessionStorage.getItem('long')

               // 初回訪問時
               if (p==null){
               setTimeout(function(){
                   sessionStorage.setItem('long', 1);
                   ipcRenderer.sendToHost('hover',"sh;clear\n")
                   ipcRenderer.sendToHost('hover', long[0])
                 },2000)
               }

               // 2回目以降訪問時
               else{
                 setTimeout(function(){
                     ipcRenderer.sendToHost('hover',"sh;clear\n")
                     ipcRenderer.sendToHost('hover', long[1])
                 },2000)
               }
   }
   else{
     p=sessionStorage.getItem('shy')

     // 初回訪問時
     i=Math.floor(Math.random() * (shy.length - 0))
     setTimeout(function(){
       ipcRenderer.sendToHost('hover',"sh;clear\n")
       ipcRenderer.sendToHost('hover', shy[i])
     },2000)
 }
}



  // if(url.indexOf(url) == -1){
  //   console.log("タイマー解除")
  //   clearInterval(timer)
  // }
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

// });
