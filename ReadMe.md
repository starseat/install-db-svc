# install-db-svc

설치된 DB에 DB 스키마 및 기초 데이터 등을 설정하기 위한 서비스 입니다.

단독 서비스로 기동되거나 `Kubernetes Object`로도 실행 가능합니다.
(`Kubernetes Object` 로 기동시 서비스는 `NodePort` 사용 권고)

<br>
`Restful API` 형식을 사용 합니다. <br>
포트번호는 `12000` 으로 사용됩니다. <br>
`Kubernetes Object` 로 서비스 되면 `NodePort` 번호는 `32700` 입니다.

  - `kubernetes Object` 의 `helm-chart` 는 [여기](http://git.bridgetec.co.kr/IPRON-CLOUD/Common/helm-chart/tree/master/charts/install-db-svc) 를 참조 하시기 바랍니다.

## Base

- `http://localhost:12000` 는 예시 `HOST` 입니다.

### Request

`http method` 는 다음과 같이 사용합니다.
- `GET`: 데이터 조회
- `POST`: 데이터 등록
- `PUT`: 데이터 수정
- `DELETE`: 데이터 삭제

### Response

응답 형식 입니다.

```
{
  "result": <Boolean>,  // true/false
  "code": <String>, // Code Keyword(3) + http status(3) + Code Number(2) (ex. COM50000)
  "status": <Number>, // http status
  "title": <String>, // result title
  "msg": <String>, // result message
  "data": <Object>, // result data 
}
```

## MongoDB

MongoDB Connection 설정 및 기초 설정을 합니다.

### Connection 설정

`MongoDB 접속 Connection`을 설정한다. <br>
미 설정시 기본값인 `mongodb://localhost:27017` 으로 사용 됩니다.

```
- uri
[POST] http://localhost:12000/db/v1/mongodb/connection

- data
{
  "connection": <Connection String>
}
```

### Connection 조회

설정된 `MongoDB Connection` 정보를 조회합니다.

```
- uri
[GET] http://localhost:12000/db/v1/mongodb/connection
```

### DB 생성 및 Collection 생성

MongoDB 에 사용될 `DB 및 Collection을 생성` 합니다.

```
- uri
[POST] http://localhost:12000/db/v1/mongodb/create/{db name}

- data
{
  "collections": [ <collection name 1>, <collection name 2>, ...]
}
```

#### Collection List

- gatewayRoute
- accountGrant
- accountPolicy
- jobScheduler
- account
- flow
- prompt
- flowdata
- group
- users
- userPasswordHistory
- accessAuth
- skills
- queue
- statusCause
- site
- trunks
- phones
- certification
- didPlan
- didPorts
- ~~carrierRoute~~
- numberPlan
- routePoints
- featureCodes
- spam
- schedule
- bridge
- tags
- firmware

### 생성된 database 및 collection 조회

```
- uri
[GET] http://localhost:12000/db/v1/mongodb/create/
```

### validation 추가

해당 collection 에 validation 추가 (json 형태)

- `<json 형태의 validation 설정 정보>` 는 [여기](http://git.bridgetec.co.kr/IPRON-CLOUD-DB/mongodb/tree/master/validation) 를 참조하시기 바랍니다.

```
- uri
[POST] http://localhost:12000/db/v1/mongodb/validation/{db name}/{collection}

- data
{
  "validation": { <json 형태의 validation 설정 정보> }
}
```

### ~~생성된 validation 조회~~

미지원

```
- uri
[GET] http://localhost:12000/db/v1/mongodb/validation/{db name}/{collection}
```

### index 추가

해당 collection 에 index 추가 (json 형태)

```
- uri
[POST] http://localhost:12000/db/v1/mongodb/index/{db name}/{collection}

- data
{
  "fields": { <collection field name 1>, <collection field name 2>, ... },
  "unique": <boolean - true|false>
}
```

### 생성된 index 조회

```
- uri
[GET] http://localhost:12000/db/v1/mongodb/index/{db name}/{collection}
```

### 기초 데이터 추가

기초 데이터 추가
- account => common_account
- user => admin@bcloud.co.kr (super admin)

```
- uri
[POST] http://localhost:12000/db/v1/mongodb/init-data/{dbname}

- data
{
  "tntId": <object Id hex string>
}
```

### 기초 데이터 추가 - custom

해당 collection 에 custom 기초 데이터 추가
- 필수값
  - data.`name` 

```
- uri
[POST] http://localhost:12000/db/v1/mongodb/init-data/{dbname}/{collection}

- data
{
  "data": <Object>  // name 필수
}
```

### 기초 데이터 조회

```
- uri
[GET] http://localhost:12000/db/v1/mongodb/init-data/{dbname}
```