# 必要なもの
- Docker
- Gradle (7.4.2)

# 立ち上げ方
1. Docker を立ち上げる
`calendar-reserve-app % docker-compose up`
2. ビルドする
`calendar-reserve-app % ./gradlew build`
3. schema.json に書かれたテーブルを作成
`java -jar tools/scalardb-schema-loader-3.5.2.jar --config app/src/main/resources/database.properties --coordinator -f tools/schema/schema.json`
4. サーバーを立ち上げる
`calendar-reserve-app % gradle run`

※ テーブルを削除したいときは，
`java -jar tools/scalardb-schema-loader-3.5.2.jar --config app/src/main/resources/database.properties  -f tools/schema/schema.json -D`
