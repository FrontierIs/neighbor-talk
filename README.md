# neighbor-talk
本システムは、4℃サイトで、sotaを使った隣人対話を実行するものです。

## 準備
1. 以下のサイトの手順に従って、electronの実行環境を作る  
https://toragramming.com/programming/electron/electron-env/
2. このリポジトリ内の4docフォルダをダウンロードし、フォルダ内に、1で作成したnode_modulesフォルダのみを移動させる
3. githubのStateHandler_javaのファイルをダウンロードする

## 実行手順 
1. Blackboardを開く  
2. Activitymanegerを起動する  
3. sotaを起動し、IPアドレスを確認する  
4. teratermを起動し、ホスト欄にIPアドレスを入力する  
5. その後、ユーザ名　root パスフレーズ edison00 を入力してOKボタンを押す　　
6. cd SotaSample/binを実行する  　　
7. ./java_run.sh jp/vstone/frontierisHotel/Rinjinを実行する    
8. sotaをもう一台起動し、4-7をもう一度実行する  　　
9. コマンドプロンプトを起動する  
10. 4docのファイルの場所まで移動する  
11. C:\Users\4docの状態で、「.\node_modules\.bin\electron .」を入力し、実行する

## 主要プログラム説明
主要プログラムについて簡単に説明する。
本システムは主に4つのプログラムで作成される。
reco2.js⇒rendere.js⇒index.js⇒blacboardの順で情報が送られる。  

### reco2.js
システムの核となるプログラム。  
ユーザのページ遷移を確認して、sotaが発話するシナリオをrenderer.jsへ送る   

### renderer.js
アイテムの特徴量が記入してあるitemlist.csvを読み込み、reco2.jsへ送る 
reco2.jsから送られてきた文字列をそのままindex.jsへ送る  

### index.js
renderer.jsから送られてきた文字列をbackboardへそのまま送る  

## その他のプログラムの説明
### index.html
ページのデザインやレイアウト、表示サイズが定義されている  
使用するPCやモニターに合わせてwebviewタグのstyleを調節

### itemlist.csv
4℃サイトのイヤリングのアイテムの特徴量が記入されている  
4℃サイトの掲載順が変更されると商品番号がずれるのでその都度書き直す必要がある




