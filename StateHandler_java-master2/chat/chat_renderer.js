const {ipcRenderer} = require('electron')

var j=0;
var loopcount=0;
var scenario={}
scenario.narrow_gold=[{"speaker":"a",
               "comment":`プレゼント何選んだらいいか分からないよね`},
               {"speaker":"b",
                "comment":`商品がたくさんあるからね`},
               {"speaker":"a",
                "comment":`そんなときは、ページの上の方の、マテリアルから絞り込むのところを`},
               {"speaker":"b",
               "comment":"マテリアルのところを?"},
               {"speaker":"a",
               "comment":"K10ホワイトゴールドにして、検索ボタンを押してね"},
               {"speaker":"b",
               "comment":"K10ホワイトゴールドだね、何がいいの?"},
               {"speaker":"a",
               "comment":"ホワイトは上品な感じをだしつつ、どんなファッションにも合わせやすいんだ"},
               {"speaker":"b",
               "comment":"なるほどなあ、K10ホワイトゴールドだね"}]
scenario.narrow_heart=[{"speaker":"a",
              "comment":`プレゼント選び、絶対に失敗したくないよね`},
              {"speaker":"b",
               "comment":`失敗したくない`},
              {"speaker":"a",
               "comment":`そんな人は、ページ上部の、モチーフから絞り込むのところを`},
              {"speaker":"b",
              "comment":"モチーフのところを?"},
              {"speaker":"a",
              "comment":"ハートにするといいよ"},
              {"speaker":"b",
              "comment":"ハートだね"},
              {"speaker":"a",
              "comment":"結局、女性はハートが好きなんだ"},
              {"speaker":"b",
              "comment":"ハート、良さそう"}]

scenario.narrow=[{"speaker":"a",
            "comment":`プレゼント、絶対に失敗したくない人、何選んだらいいかわからない人におすすめ`},
            {"speaker":"b",
             "comment":`なになに?`},
            {"speaker":"a",
             "comment":`ページ上部のマテリアルから絞り込むのところを`},
            {"speaker":"b",
            "comment":"マテリアルのところを?"},
            {"speaker":"a",
            "comment":"K10ホワイトゴールドにして検索ボタンを押してね"},
            {"speaker":"b",
            "comment":"K10ホワイトゴールドだね、何がいいの?"},
            {"speaker":"a",
            "comment":"ホワイトは上品な感じをだしつつ、どんなファッションにも合わせやすいんだ"},
            {"speaker":"b",
            "comment":"なるほどなあ"},
            {"speaker":"a",
            "comment":"しかもコーティングされていて、長く使っても変色しにくいんだ"},
            {"speaker":"b",
            "comment":"K10ホワイトゴールド、だね、探してみるよ"}]

scenario.cute=[{"speaker":"a",
              "comment":`キュートなデザインだね`},
              {"speaker":"b",
               "comment":`女性らしくていいなあ`},
              {"speaker":"a",
               "comment":`コーティングもされてて、長く使ってもらえそうだね`},
              {"speaker":"b",
              "comment":"ワンポイントのハートもいい感じ"}]

scenario.long=[{"speaker":"a",
              "comment":`長いイヤリングだね`},
              {"speaker":"b",
               "comment":`シルバーに輝いてる長いイヤリングだね`},
              {"speaker":"a",
               "comment":`でも女性からすると、イヤリングが長いと、揺れて邪魔みたいだよ。`},
              {"speaker":"b",
              "comment":"なるほど、長いイヤリングはさけたほうがいいみたいだね"}]

scenario.long2=[{"speaker":"a",
              "comment":`おっ、また長いイヤリングだね`},
              {"speaker":"b",
               "comment":`女性からすると、イヤリングが長いと、揺れて邪魔みたいなんだよね`},
              {"speaker":"a",
               "comment":`長いのもいいけど、他のも見てみようかな`}]

scenario.nega_long=[{"speaker":"a",
            "comment":`僕の友だちが、ちょっと長めなイヤリングを彼女にプレゼントしたんだよ。`},
            {"speaker":"b",
             "comment":`うん、うん、ゴージャスだもんね`},
            {"speaker":"a",
             "comment":`でも、その彼女は1回つけただけで、全然つけてくれなかったんだよ`},
            {"speaker":"b",
            "comment":"なんだって、真剣に選んだのに、どうして?"},
            {"speaker":"a",
            "comment":"長いイヤリングが揺れるのが邪魔だったみたいだよ。"},
            {"speaker":"b",
            "comment":"なるほど、長いイヤリングはさけたほうがいいみたいだね"}]
scenario.simple=[{"speaker":"a",
            "comment":`シンプルなデザインのもの、いいなあ`},
            {"speaker":"b",
             "comment":`派手なものは人を選んじゃうからね`},
            {"speaker":"a",
             "comment":`これだと、どんな女性にも似合いそうだね`},
            {"speaker":"b",
            "comment":"女性らしさと上品さがあって、すごくいいなあ"}]

scenario.paru=[{"speaker":"a",
            "comment":`パールのイヤリングだね`},
            {"speaker":"b",
             "comment":`お手入れが大変かもね`},
            {"speaker":"a",
             "comment":`そうだね、他の商品も見てみよう`}]
scenario.paru2=[{"speaker":"a",
         "comment":`パールを使ったイヤリングかあ`},
         {"speaker":"b",
          "comment":`すごく光ってそう`},
         {"speaker":"a",
          "comment":`ホワイトゴールドにコーティング加工がされているよ`},
         {"speaker":"b",
           "comment":`ただ長時間つけてると、耳が疲れちゃうかもなあ`}]

scenario.shizuku=[{"speaker":"a",
         "comment":`4ドシーのブランドコンセプトって何か知ってる?`},
         {"speaker":"b",
          "comment":`なに、なに`},
         {"speaker":"a",
          "comment":`水だよ、水を象徴するしずくのデザインがコンセプトだよ。`},
         {"speaker":"b",
           "comment":`なるほどなあ、他のモチーフも探してみようかな`}]
scenario.shizuku2=[{"speaker":"a",
                    "comment":`4ドシーのブランドコンセプトのしずくをモチーフにしたデザインだね`},
                    {"speaker":"b",
                     "comment":`クールな印象だね`},
                    {"speaker":"a",
                     "comment":`ただちょっとクールすぎるかなあ`},
                    {"speaker":"b",
                      "comment":`なるほどなあ、他のモチーフも探してみようかな`}]
scenario.shy=[{"speaker":"a",
                    "comment":`小さめのイヤリングだね`},
                    {"speaker":"b",
                     "comment":`つけやすそうではあるね`},
                    {"speaker":"a",
                     "comment":`ただ、ちょっと大人しすぎないかな`},
                    {"speaker":"b",
                      "comment":`うーん、もう少し華やかなのにしようかな`}]
scenario.dis=[{"speaker":"a",
              "comment":`このイヤリングどうかな?`},
              {"speaker":"b",
               "comment":`悪くはなさそう、ただもうちょっと、女性らしさがあったほうがいいかもね`},
              {"speaker":"a",
               "comment":`うーん、難しいなあ`}]

scenario.gold=[{"speaker":"a",
              "comment":`イヤリングって素材もいろいろあるんだね`},
              {"speaker":"b",
               "comment":`どんな素材が良さそうかな`},
              {"speaker":"a",
               "comment":`ゴールドなんてどう?`},
               {"speaker":"b",
                "comment":`ゴールドだね、何がいいの?`},
               {"speaker":"a",
                "comment":`上品な感じを出しつつ、どんなファッションにも合わせやすそう`},
                {"speaker":"b",
                 "comment":`なるほどなあ`}]

scenario.silver=[{"speaker":"a",
              "comment":`シルバーを使ったイヤリングかあ`},
              {"speaker":"b",
               "comment":`シルバーはアレルギーを起こしにくい金属なんだって`},
              {"speaker":"a",
               "comment":`へー、どんな相手にも贈りやすそう`}] 


var createMsgDiv = function(text) {
   $message = $('<div>').addClass('msg');
   var texts = text.split(/\\n|\r\n|\n/);
   for (var p in texts) $message.append('<p>' + texts[p] + '</p>');
   // var date = new　Date();
   // var hours = ("0" + date.getHours()).slice(-2);
   // var minutes = ("0" + date.getMinutes()).slice(-2);
   // $message.append('<time>' + hours + ':' + minutes + '</time>');
   return $message;
}
var createAvatarDiv = function(userId) {
 $avatar = $('<div>').addClass('avatar');
 if (userId == 'a') {
   $avatar.append('<img src="C:/Users/Frontier-is/Desktop/sota/chat/boya.png" draggable="false"/>');

 }
 else{
   $avatar.append('<img src="C:/Users/Frontier-is/Desktop/sota/chat/boyc.png" draggable="false"/>');
 }
 console.log("ABATAR追加")
 return $avatar;
}
var addSelf = function(userId, text) {
   $self = $('<li>').addClass('self');
   $message = createMsgDiv(text);
   $avatar = createAvatarDiv(userId);
   $self.append($message);
   $self.append($avatar);
   $('.chat').append($self).trigger('create');
};
var addOther = function(userId, text) {
   $other = $('<li>').addClass('other');
   $avatar = createAvatarDiv(userId);
   $message = createMsgDiv(text);
   $other.append($message);
   $other.append($avatar);
   $('.chat').append($other).trigger('create');
};

let webview=document.getElementById("wv");
// 同期
// console.log(ipcRenderer.sendSync('synchronous-message', 'ping')) // pong
console.log("test")
// 非同期のほうがいい
ipcRenderer.on('asynchronous-reply', (event, arg) => {
  console.log(arg) // pong
})


function back(){
  webview.goBack()
}

function opendev(){
  webview.openDevTools();
}

var i=0;
webview.addEventListener("did-finish-load", function(){
  webview.send("from_renderer","fromrenderer")
});

webview.addEventListener("ipc-message", (e) => {
  // 曲の情報
  if (e.channel === "hover") {
    cut=e.args[0].slice(0,-1)
    // cut=e.args.slice( 0, -2 )
    console.log(cut)
    topic=cut.slice(3)
    console.log(topic)
    console.log(scenario[topic])
    // jsonぽく扱いたいため、今回こうした。
    console.log("id実行")
    parent=document.getElementsByClassName("chat")[0]
    console.log(parent)
    while(parent.lastChild){
      parent.removeChild(parent.lastChild);
      console.log(parent)
    }
    var len =scenario[topic].length;
    console.log(len)
    var id=setInterval(function(){
      console.log("chat追加");
      if(scenario[topic][i].speaker=="a"){
          addSelf("a",scenario[topic][i].comment);
        }

        else {
          console.log("other追加");
          addOther("b",scenario[topic][i].comment);
        }
        //えらーでたからコメントアウト
        $('.chat').animate({scrollTop: $('.chat')[0].scrollHeight}, 'fast');
        i=i+1;
        if(i==len){
          //loopcount++;
          //if(loopcount==1){
            clearInterval(id);
            console.log("会話終了")
        //  }
          i=0;
        }

    },4000);

    // ipcRenderer.send('asynchronous-message', e.args[0])
  }

});

//会話追加

// var rand=4500;
