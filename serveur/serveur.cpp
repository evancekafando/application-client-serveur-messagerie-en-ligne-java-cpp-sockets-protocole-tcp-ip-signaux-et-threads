 //fichiers include
#include <string.h>        //pour les strings
#include <netinet/in.h>       // connection internet
#include <sys/socket.h>  //librairie pour les sockets
#include <arpa/inet.h> //
#include <unistd.h> //librairie pour les fonction close() et gethostname()
#include <iostream> 
#include <fstream> 
#include <sstream> 
#include <stdio.h>
#include <sys/types.h>
#include <fcntl.h>
#include <netdb.h>
#include <stdlib.h>
#include <pthread.h>
#include <errno.h>
#include <signal.h>
#include <signal.h>
#include <set>

using namespace std; 

//----- Defines -------------------------------------------------------------
#define  PORT_NUM   1550      // numero de port du serveur
#define  MAX_LISTEN    20       //  nombre max d'ecoute pour le serveur

//--------variables globales-------------------------------------------------

unsigned int         server_s,  client_s;      // socket d'écoute et socket d'échange entre le serveur et le client
struct sockaddr_in   server_addr;             // adresse du serveur (toutes les informations )
struct sockaddr_in   client_addr;             // adresse du client pour la connection (toutes les informations)
struct in_addr       client_ip_addr;           //structure pour l'adresse IP du client         
unsigned int         addr_len;        

pthread_mutex_t mutex_state = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t mutex_state1 = PTHREAD_MUTEX_INITIALIZER;

pthread_t thread[MAX_LISTEN];

set<unsigned int> connexion;
set<unsigned int>:: iterator it;


void *server_send(int client, char data []){
     for(it = connexion.begin(); it!=connexion.end(); it++){
	send(*it, data, strlen(data),0);
     }
}

void fonction (int sig) 
{
   char out_buf[100];  
   strcpy(out_buf, "System down ");
   for(it = connexion.begin(); it!=connexion.end(); it++){
	send(*it, out_buf, strlen(out_buf),0);
        close(*it);
        connexion.erase(it);
        pthread_join(thread[*it],NULL);
   }

 exit(0);
}

void *send_new_user(void *arg){
    char out_buf[100];
    int client = (int)arg;
    strcpy(out_buf, "Nouveau User connecte");
    pthread_mutex_lock(&mutex_state);
            for(it = connexion.begin(); it!=connexion.end(); it++){
	        if(client!=*it)send(*it, out_buf, strlen(out_buf),0);
            }
            memset(out_buf, 0, sizeof(out_buf));  
    pthread_mutex_unlock(&mutex_state);
    pthread_join(thread[client*2],NULL);
}

void *server_read(void *arg){
	char buffer[1024];
	int bufferlen;
	int client = (int)arg;
	for(;;)
	{

		bufferlen = read(client, buffer, sizeof(buffer));
		if (bufferlen <= 0) {
                   cout << "client deconnecte :" << client << endl ;
                   connexion.erase(client);
                   close(client);
                   pthread_exit(NULL);
                }
		pthread_mutex_lock(&mutex_state);
		server_send(client, buffer);
                memset(buffer, 0, sizeof(buffer));
		pthread_mutex_unlock(&mutex_state);
         }
}



//=====Programme principal=====================================================
int main ()
{
signal(SIGINT,fonction);

// creation d'un socket du serveur montrant que nous utilisons internet et le protocol TCP
  server_s = socket(AF_INET, SOCK_STREAM, 0);

// initalisation de la socket de depart pout permettre au serveur d'écouter
  server_addr.sin_family      = AF_INET;            // 
  server_addr.sin_port        = htons(PORT_NUM);    // 
  server_addr.sin_addr.s_addr = htonl(INADDR_ANY);  // 
//assignation de la socket au serveur
  bind(server_s, (struct sockaddr *)&server_addr, sizeof(server_addr));

// debut de l'écoute du serveur
  listen(server_s, MAX_LISTEN);

//debut de l'execution du pere (serveur)
  while (1)
  { 
	// demande detecter et acceptation de la connection entre le serveur et le client, creation d'une socket avec les memes propiertes que la premiere. celle ci sera la socket d'échange entre les deux
	  addr_len = sizeof(client_addr);
	  client_s = accept(server_s, (struct sockaddr *)&client_addr, &addr_len);
          connexion.insert(client_s);
	  cout << "client_s dans le pere = "<< client_s <<endl;
          pthread_create(&thread[client_s*2], NULL, send_new_user,(void *) client_s);
	  pthread_create(&thread[client_s], NULL, server_read,(void *) client_s); 
  }
  close (server_s); //fermeture de la socket d'écoute par le serveur
   return 0;
}
