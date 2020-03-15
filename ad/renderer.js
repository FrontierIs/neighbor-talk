
const {ipcRenderer} = require('electron')

let webview=document.getElementById("wv");
// 同期
// console.log(ipcRenderer.sendSync('synchronous-message', 'ping')) // pong
console.log("test")
// 非同期のほうがいい
ipcRenderer.on('asynchronous-reply', (event, arg) => {
  console.log(arg) // pong
})

items=getCsv("itemlist.csv")

// items[0]=["No","長い","短い","ハート","しずく","白い","赤い","青い","ゴールド","シルバー"]
console.log(items)


function back(){
  webview.goBack()
}

function opendev(){
  webview.openDevTools();
}



// var form = document.forms.myform;
// form.myfile.addEventListener('change', upload, false);
//
// function upload(evt) {
// 	if (!isFileUpload()) {
// 		console.log('お使いのブラウザはファイルAPIがサポートされていません');
// 	} else {
// 		var data = null;
// 		var file = evt.target.files[0];
// 		reader = new FileReader();
// 		reader.readAsBinaryString(file);
// 		reader.onload = function(event) {
// 			var result = event.target.result;
// 			var sjisArray = str2Array(result);
//       console.log(sjisArray)
// 			var uniArray = Encoding.convert(sjisArray, 'UNICODE', 'SJIS');
// 		  var result = Encoding.codeToString(uniArray);
// 		  console.log(result); //csvデータ(string)
//
// 		};
// 		reader.onerror = function() {
// 			console.log('ファイルが読み込めませんでした。 ' + file.fileName);
// 		};
// 	}
// }
//
// function isFileUpload() {
// 	   var isCompatible = false;
// 	   if (window.File && window.FileReader && window.FileList && window.Blob) {
// 	   isCompatible = true;
// 	   }
// 	   return isCompatible;
// }
//
// function str2Array(str) {
//     var array = [],i,il=str.length;
//     for(i=0;i<il;i++) array.push(str.charCodeAt(i));
//     return array;
// }

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
var i=0

webview.addEventListener("did-finish-load", function(){
  if(i==0){
    webview.send("from_renderer",items)
    console.log(items)
    i=1
  }
});

webview.addEventListener("ipc-message", (e) => {
  // 曲の情報
  if (e.channel === "hover" && e.args[0]!="sh;clear" && e.args[0]=="sh;4doc") {
    // jsonぽく扱いたいため、今回こうした。
    console.log(e.args[0])
    cut=e.args[0].slice(0,-1)
      // cut=e.args.slice( 0, -2 )
    console.log(cut)
    topic=cut.slice(3)
    console.log(topic)
    ipcRenderer.send('asynchronous-message', e.args[0])
    ads=document.getElementsByClassName('ad')
    if(topic=="kanren"){
      i=Math.floor(Math.random() * (10 - 0))
      if(i%2==0){
        topic="ok_long"
      }
      else{
        topic="ok_silver"
      }
    }
    string=" <img src='gif/"+topic+".gif' width='600px' height='150px'>"
    console.log(string)
    ads[0].innerHTML=string
  }

});
