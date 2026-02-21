Guide to deploy: 

Open 2 terminals. 

First terminal(basic server):
    ssh -L 37419:localhost:37419 s489889@helios.cs.ifmo.ru -p 2222

Second terminal(manager side):
    ssh -L 37420:localhost:37420 s489889@helios.cs.ifmo.ru -p 2222