to deploy in localhost(docker):
1) docker-compose up --build
2) go to localhost:8080

to deploy in helios:
1) ssl -L 37419:localhost:37419 s489889@helios.cs.ifmo.ru -p 2222
2) httpd -f ~/httpd-root/conf/httpd.conf -k start
3) java -jar -DFCGI_PORT=31992 ~/httpd-root/fcgi-bin/app.jar
3852