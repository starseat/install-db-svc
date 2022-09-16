# install-db-svc

설치된 DB에 DB 스키마 및 기초 데이터 등을 설정하기 위한 서비스 입니다.

단독 서비스로 기동되거나 `Kubernetes Object`로도 실행 가능합니다.
(`Kubernetes Object` 로 기동시 서비스는 `NodePort` 사용 권고)

<br>
`Restful API` 형식을 사용 합니다. <br>
포트번호는 `12000` 으로 사용됩니다. <br>



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

`MongoDB 접속 Connection`을 설정합니다. <br>
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



### 생성된 database 및 collection 조회

```
- uri
[GET] http://localhost:12000/db/v1/mongodb/create/
```



### validation 추가

해당 collection 에 validation 추가 (json 형태)

```
- uri
[POST] http://localhost:12000/db/v1/mongodb/validation/{db name}/{collection}

- data
{
  "validation": { <json 형태의 validation 설정 정보> }
}
```



#### validation 설정 정보

validation 예시 입니다. <br>
자세한 사항은 [여기](https://www.mongodb.com/docs/manual/core/schema-validation/#when-mongodb-checks-validation)를 참조해 주세요.

```json
{
  "collMod": "users",
  "validationAction": "warn",
  "validator": {
    "$jsonSchema": {
      "bsonType": "object",
      "required": [
        "email",
        "name"
      ],
      "properties": {
        "email": {
          "bsonType": "string",
          "pattern": "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?",
          "minLength": 1,
          "maxLength": 128,
          "description": "please check email format. (kor, eng, number, _, -, .)[1-128]"
        },
        "name": {
          "bsonType": "string",
          "pattern": "^[ㄱ-ㅎ|가-힣|a-z|A-Z|0-9|_|\\-|\\.| ]+$",
          "minLength": 1,
          "maxLength": 128,
          "description": "please check name format. (kor, eng, number, _, -, .)[1-128]"
        },
        "password": {
          "bsonType": "string",
          "minLength": 4,
          "maxLength": 128,
          "description": "please check password format. [4-128]"
        },
        "tags": {
          "bsonType": "array"
        },
        "level": {
          "bsonType": "string",
          "enum": [ "superadmin", "admin", "manager", "user"]
        },
        "extension": {
          "bsonType": "string",
          "pattern": "^[0-9|\\*|\\#]+$",
          "minLength": 2,
          "maxLength": 12,
          "description": "please check extension format. (number, *, #)[2-12]"
        },
        "skillSets": {
          "bsonType": "array",
          "items": {
            "bsonType": "object",
            "properties": {
              "skillLv": {
                "bsonType": "int",
                "minimum": 0,
                "maximum": 99,
                "description": "please check skillLv format. [0-99]"
              }, 
              "skillPri": {
                "bsonType": "int",
                "minimum": 0,
                "maximum": 9,
                "description": "please check skillPri format. [0-9]"
              }
            }
          }
        },
        "schedule": {
          "bsonType": "object",
          "properties": {
            "enable": {
              "bsonType": "bool"
            },
            "type": {
              "bsonType": "string",
              "enum": [ "user", "group"]
            },
            "medias": {
              "bsonType": "array",
              "items": {
                "bsonType": "object",
                "properties": {
                  "mediaType": {
                    "bsonType": "string",
                    "enum": [ "voice", "video", "chat", "email"]
                  }, 
                  "scheduleId": {
                    "bsonType": "objectId"
                  },
                  "enable": {
                    "bsonType": "bool"
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

```



### ~~생성된 validation 조회~~

현재 미지원

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