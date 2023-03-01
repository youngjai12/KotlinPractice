## Practice
kotlin 및 Spring에 관련한 여러 기술들을 시험해보는 repo. 
Test 코드를 잘 만듬으로써 시험해보는 경우도 많은데, 이떄 mockServer 만드느것 신경쓰기. 

## 다뤘던 내용 (기술적인 test) 
#### autoConfiguration 

* webclient 는 같지만, 하나는 로컬, 하나는 외부로 해서 client 설정이 다른것         
   -> 이때 서로다른 config POJO인데, 합쳐서 bean으로 등록하려하면, 컴파일오류는 안나지만, 잘안됨.          
   -> 그래서 결국 서로다른 config이면 그냥 다른파일로, 다른 객체로 분리해야함. 
* Test시에 secret한 내용들 하드코딩하지 않고, .yml로 정의된것 load해서 쓰기 

#### interface & subclass 
* subclass 갖는 interface를 webclient의 결과물로 deserialize할 수 있게하기(2가지)
    * @JsonSubType으로 구분자 column 정해서 구체적인 구현체로 deserialize
    * Customized된 deserializer를 따로 구현해서, webclient codec에 붙이는 방향

#### WebClient's Asynchronous & non-blocking Call
* 위의 webclient 호출결과를 async-nonBlocking하게 동시다발적으로 호출하기 

#### MultiThread Programming - @Scheduled
* @Scheduled 만 붙이고, default 조건으로 쓰는것 
* SchedulingConfigurer 을 bean으로 주입하도록 class 생성 -> default로 single thread 말고, multi-thread로 구현하려고..! 
	* 이 떄, ScheduledExecutorService 을 bean으로 등록하고, 
	* 이것 기준으로 scheduled된 것들 shutdown하거나, 다시 schedule하는것 구현 
