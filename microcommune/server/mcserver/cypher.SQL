
MATCH n
RETURN n

/***************************************
 *     schema
 ***************************************/
CREATE INDEX ON :Account(uid);
CREATE INDEX ON :Account(phone)
CREATE INDEX ON :Account(nickName);
CREATE INDEX ON :Account(status);

CREATE INDEX ON :Circle(rid);

CREATE INDEX ON :Community(cid)
CREATE INDEX ON :Community(name)


/***************************************
 *     Account
 ***************************************/

MATCH (account:Account)
WHERE account.phone="121"
RETURN account

MATCH (account:Account)
WHERE account.phone="121" AND account.status="active"
RETURN account

CREATE (account:Account{phone:"121",status:"init",head:"abc",nickName:"小黑",
                        mainBusiness:"送水",byPhone:"checked",byScan:"checked",byScanNearBy:"allowed",code:"1234",time:"new Date().getTime()"})
SET account.uid=ID({phone:"121",status:"init",head:"abc",nickName:"小黑",
                        mainBusiness:"送水",byPhone:"checked",byScan:"checked",byScanNearBy:"allowed",code:"1234",time:"new Date().getTime()"})
RETURN account

/***************************************
 *     Circle
 ***************************************/

 MATCH (circle:Circle)
 WHERE circle.rid=10
 SET circle.name="朋友"
 RETURN circle

 MATCH other-[r]-(circle:Circle)
 WHERE circle.rid=10
 DELETE circle,r
 RETURN circle,r

 MATCH (circle:Circle)-[r:HAS_FRIEND]->(account:Account)
 WHERE circle.rid=10 AND account.phone="121"
 DELETE r
 RETURN circle

 START circle=node(10)
 MATCH (account:Account)
 WHERE account.phone="121"
 CREATE UNIQUE circle-[r:HAS_FRIEND]->account
 RETURN r

MATCH (account:Account)
WHERE account.phone="121"
CREATE UNIQUE account-[r:HAS_CIRCLE]->(circle:Circle{name:"同学"})
SET circle.rid=ID({name:"同学"})
RETURN circle

/***************************************
 *     Community
 ***************************************/

 CREATE (community:Community{name:"天通苑站",description:"很大",location:""})
 SET community.cid=ID({name:"天通苑站",description:"很大",location:""})
 RETURN community

 MATCH (community:Community)
 RETURN community

 START community=node(15)
 MATCH (account:Account)
 WHERE account.phone="121"
 CREATE account-[r:JOIN]->community
 RETURN r

 MATCH (account:Account)-[r:JOIN]->(community:Community)
 WHERE account.phone="121" AND community.cid=15
 DELETE r

 MATCH (account:Account)-[r:JOIN]->(community:Community)
 WHERE account.phone="121"
 RETURN community

  MATCH (account:Account)-[r:JOIN]->(community:Community)
  WHERE community.name="天通苑站"
  RETURN account

/***************************************
 *     Relation
 ***************************************/

MATCH (account1:Account),(account2:Account)
WHERE account1.phone="121" AND account2.phone="122" AND account2.byPhone IN ["allowed","checked"]
CREATE UNIQUE account1-[r:FRIEND]->account2
RETURN  account2, r

START circle=node(10)
MATCH (account:Account)
WHERE account.phone="121"
CREATE UNIQUE circle-[r:HAS_FRIEND]->account
RETURN  r

MATCH (account1:Account)-[r:FRIEND]-(account2:Account)
WHERE account1.phone="121" AND account2.phone="122"
RETURN r

MATCH (account:Account)-[r:HAS_CIRCLE]->(circle:Circle)-[r1:HAS_FRIEND]->(account1:Account)
WHERE account.phone="121" AND account1.phone="122"
DELETE r1
RETURN account

MATCH (account1:Account)-[r:FRIEND]-(account2:Account)
WHERE account1.phone="121"
RETURN account2

MATCH (account:Account)-[:HAS_CIRCLE]->(circle:Circle)
WHERE account.phone="121"
RETURN circle

MATCH (account:Account)-[r:FRIEND]-(account1:Account)
WHERE account.phone="121" AND r.friendStatus IN ["success","delete","blacklist"]
RETURN r, account1

MATCH (account1:Account)-[r1:HAS_CIRCLE]->(circle:Circle)-[r2:HAS_FRIEND]->(account2:Account)
WHERE account1.phone="121"
RETURN circle, account2

MATCH (account1:Account)-[r:FRIEND]->(account2:Account)
WHERE account2.phone="121" AND account1.phone="122"
RETURN r

START circle=node(10})
MATCH (account:Account)
WHERE account.phone="121"
CREATE UNIQUE circle-[r:HAS_FRIEND]->account
RETURN r

MATCH (account1:Account)-[r:FRIEND]->(account2:Account)
WHERE account2.phone="121" AND account1.phone="122"
DELETE r
RETURN r

MATCH (circle:Circle)-[r:HAS_FRIEND]->（account:Account)
WHERE circle.rid=10 AND account.phone="121"
DELETE r
RETURN r

MATCH (account1:Account)-[r:FRIEND]->(account2:Account)
WHERE account2.phone="121" AND r.friendStatus="init"
RETURN account1, r










