## Applications client-serveur : Messagerie en Ligne - Java/C++

**I. Description :**
Il s'agit d'implémenter une application client-serveur où des groupes de clients
dialoguent entre elles par voix ou texte par l'intermédiaire d’un serveur qui permet de gérer ces dialogues.

L’**application** **cliente** a été programmée en **Java**.

Du coté de l’*application serveur*, le programme a été écrit en **C/C++** sur **Linux**. **Le principe de fonctionnement de cette application repose sur la communication entre processus. Cette communication peut être effectuée en utilisant les files de messages, la mémoire partagée, ou les signaux.**

**II. Principe de fonctionnement du Serveur :**
***Utilisation d’une file de message :***
**Création d’une file de message :** Avant de commencer à programmer votre serveur, créer d’abord une file de message. Cette file sera créée une fois pour toute et servira à recevoir des messages des clients participant aux différents forums.

**Serveur** : Au début le serveur **(processus Serveur ou le processus père)** va se mettre en attente de connexions de clients. Lorsqu'un client numéro **i** désire participer à la discussion dans un forum donné, le **processus serveur** fait appel à la commande ***accept()*** pour établir un lien avec ce client. Cette commande renvoie le descripteur de la **socket** (un entier) qui va permettre la communication entre le client **i** et le serveur. La valeur associée à ce descripteur est **différente** des valeurs des descripteurs des autres clients en discussions. Une liste de connexion (**tableau de type entier**) est alors nécessaire pour chaque forum pour stocker (**à chaque nouvelle connexion**) les descripteurs des **sockets** des différents clients en discussions afin que le serveur puisse les identifiés. Cette liste doit être mise à jour à chaque arrivée (**nouvelle connexion**) et départ (**déconnexion**) d’un client du forum.

À chaque nouvelle connexion dans un forum, le ***processus Serveur*** insère le descripteur de cette nouvelle connexion dans la ***liste des connexions***, puis attend une nouvelle demande. Le serveur crée ensuite un ***processus fils***. Le fils se met dans une boucle infinie et attend de recevoir de l’information du client (la commande ***recv()***) :

- Si le message reçu est ‘***bye***’ i.e. fin de la connexion, le processus fils doit
avertir le processus serveur pour qu’il ferme la **socket** du client qui vient de se déconnecter et ensuite, pour qu’il l'enlève de ***la liste des connexions***. Cet avertissement (communication entre le ***processus fils et le processus serveur***) peut être émis en utilisant les **signaux**. Le processus fils doit ensuite terminer normalement.

- Si le message reçu est autre que ‘***bye***’, le ***processus fils*** va placer ce message dans la file de messages. Il va ensuite avertir le ***processus serveur*** pour qu’il récupère le message de la file et l’envoyer à tout les clients qui participent dans ce forum (on utilisera pour cela la ***liste des connexions***). Ici encore, on utilisera les ***signaux pour la communication entre le processus fils et le processus serveur.***

Si on arrête le ***serveur*** (**CTR C**) avant de fermer les connexions avec les clients en discussions, l’application risque de ne pas fonctionnée correctement lorsqu’on relance le ***serveur***. Pour résoudre ce problème, on va par exemple utiliser la procédure ci-dessous :

- lorsque le processus serveur reçoit le signal ***SIGINT*** (**CTR C**), il va envoyer un message (message de type «**serveur down**») à tous les clients participant aux différents, ensuite, il va fermer les **sockets** de tout les clients, et finalement, fermera la ***socket*** du ***serveur***.

**III. Principe de fonctionnement du client :**
Le rôle de l’application cliente est de saisir des messages voix ou texte de l'utilisateur et de recevoir les messages venant d'autres clients. Au début, le client doit entrer son ’Nike Name’, choisir un forum (les autres doivent être inactif) et se connecter au serveur. Il doit recevoir une confirmation du serveur comme quoi la connections est bien établie. Chaque fois qu’un client veut envoyer un message aux autres clients du forum, il place toujours son nickname avant le message.

![](https://imgur.com/JjFnOlL.png)

