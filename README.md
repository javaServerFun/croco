# Build a Crocodile game server

Game is simple two players tell number.
Bigger wins.

You should implement a rest server like below:

POST /croco/{playerName}/{number}

example usage

curl -d '' http://localhost:8080/croco/Irek/615

in other (parallel) bash session

curl -d '' http://localhost:8080/croco/Arek/620


Game shoud say who wins


Start coding in:
[Server](/src/main/java/pl/setblack/croco/Server.java)


## Help
As always there is a [solution](../../tree/solution) branch
