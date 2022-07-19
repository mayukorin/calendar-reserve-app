# 必要なもの
- Docker
- Gradle (7.4.2)

# 立ち上げ方
1. Docker を立ち上げる．立ち上がったままで OK.

`calendar-reserve-app % docker-compose up`

2. 1 とは別のタブで下のコマンドを実行．

`calendar-reserve-app % ./gradlew build`

3. 2 と同じタブで，schema.json に書かれたテーブルを作成

`java -jar tools/scalardb-schema-loader-3.5.2.jar --config app/src/main/resources/database.properties --coordinator -f tools/schema/schema.json`

4. 2 と同じタブで，サーバーを立ち上げる

`calendar-reserve-app % gradle run`

5. 3 とは別のタブで，以下のコマンドを実行しテーブルに初期データを入れる．`"initial data loaded"` と返ってきたらOK.

`calendar-reserve-app % curl -X POST -H "Content-Type: application/json" http://localhost:8090/load_inini`


※ テーブルを削除したいときは，
`java -jar tools/scalardb-schema-loader-3.5.2.jar --config app/src/main/resources/database.properties  -f tools/schema/schema.json -D`

# ファイルを編集したときは
その都度，以下2つのコマンドを実行する．こうすることで，変更が反映される．
1. ビルドする
`./gradlew build`
2. サーバーを立ち上げる．
`gradle run`