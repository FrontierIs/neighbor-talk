

// Electron のモジュールを取得
const {app, BrowserWindow,ipcMain} = require('electron')

// メインウィンドウはグローバル参照で保持
// 空になれば自動的にガベージコレクションが働き、開放される
let mainWindow

const net = require( 'net' );
const bbClient = new net.Socket();
// let reconnectId = null;
// let sotaReconnectCount = 0;
bbClient.connect( 11000,"localhost", null);
// function connectSota(host=config.sota.host, port=config.sota.port) {
//     sotaReconnectCount++;
//
// }
bbClient.on('connect', function () {
    bbClient.write("name;op\n")
    // logger.info( 'sota connected.' );
    // mainWindow.webContents.send('monitoring', 'sota connected.');
    // if (reconnectId) {
    //     clearInterval(reconnectId);
    //     reconnectId = null;
    // }
    // sotaReconnectCount = 0;
    // // アラートを消す
    // writeMultiple_('action=1 delay=60 color=3\n');
    // console.log('action=1 delay=60 color=3');
});
bbClient.on('error', function(ex) {
    // logger.error(ex);
});
//. 接続が切断されたら、その旨をメッセージで表示する
bbClient.on( 'close', function(){
    // logger.warn( 'sota closed.' );
    // mainWindow.webContents.send('monitoring', 'sota closed.');
    // if (sotaReconnectCount === 10) {
    //     mainWindow.webContents.send('monitoring', 'sota reconnection failed 10 times.');
    //     // アラートを表示
    //     writeMultiple_('action=61 delay=60 color=2\n');
    //     logger.error('sota reconnection failed 10 times.');
    // }
    // if (!reconnectId) {
    //     reconnectId = setInterval(connectSota, 10000);
    // }
});
// connectSota();

// Electron のウィンドウを生成する関数
function createWindow () {
  // ウィンドウ生成（横幅 800、高さ 600、フレームを含まないサイズ指定）
  mainWindow = new BrowserWindow({width: 800, height: 1200,webPreferences:{webviewTag: true, nodeIntegration: true}})

  // 表示対象の HTML ファイルを読み込む
  mainWindow.loadFile('chat_index.html')

  // ウィンドウを閉じた時に発生する処理
  mainWindow.on('closed', () => {
    // メインウィンドウの値を null にして、ガベージコレクタに開放させる
    mainWindow = null
  })
}

// Electronの初期化完了後に、ウィンドウ生成関数を実行
app.on('ready', createWindow)


// ↓↓ アプリが macOS で実行された際の対応（クロスプラットフォーム対応）

// 全てのウィンドウが閉じたときに発生
app.on('window-all-closed', () => {
  // macOS の場合、アプリを完全に終了するのではなく
  // メニューバーに残す（ユーザーが Ctrl + Q を押すまで終了しない）ことが
  // 一般的であるため、これを表現する
  if (process.platform !== 'darwin') {
    app.quit()
  }
})

// アプリが実行された時に発生
app.on('activate', function () {
  // macOS の場合、アプリ起動処理（Dock アイコンクリック）時に
  // 実行ウィンドウが空であれば、
  // アプリ内にウィンドウを再作成することが一般的
  if (mainWindow === null) {
    createWindow()
  }
})

// const {ipcMain} = require('electron')

// 同期
// ipcMain.on('synchronous-message', (event,4doc> {
//   console.log(arg)  // ping
//   event.returnValue = 'pong'
//
// })

// 非同期
ipcMain.on('asynchronous-message', (event, arg) => {
  console.log(arg)  // ping
  // event.sender.send('asynchronous-reply', 'pong')
  bbClient.write(arg)
  //blackbordに投げるコメントを編集
})
