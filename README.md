# 📌 Accademia Arcana

Gioco di ruolo (RPG) a turni con interfaccia grafica JavaFX, sviluppato in Java per il corso di
Metodologie di Programmazione (Università di Camerino, matricola 195945).
Il giocatore crea uno studente di magia, sceglie un'affinità elementale e affronta cinque anni
all'Accademia: ogni settimana segue lezioni, si allena o duella, combinando elementi e rune in
incantesimi, e a fine anno deve battere il guardiano della Prova dell'Equinozio per andare avanti.
La partita si salva automaticamente e si può riprendere dal menu.

La documentazione completa (funzionalità, classi e responsabilità, persistenza, meccanismi di
estensione, uso dell'AI) è nella [Wiki](../../wiki) del repository.

## 🚀 Come eseguire il progetto

### Prerequisiti

* Java 25 (LTS) — il progetto compila per Java 21, quindi funziona con qualsiasi JDK dal 21 in poi
* Gradle — non serve installarlo: il repository contiene il Gradle Wrapper (`gradlew`), che scarica
  automaticamente Gradle e JavaFX

### Istruzioni

```
git clone https://github.com/cristianociuccarelli-stack/ProgettoModellazioneeGestioneDellaConoscenza.git
cd ProgettoModellazioneeGestioneDellaConoscenza
```

### Build del progetto

```
./gradlew build
```

La build compila il codice ed esegue i test automatici (JUnit 5).

### Esecuzione

```
./gradlew run
```

Su Windows usare `gradlew.bat build` e `gradlew.bat run`.

La partita viene salvata nel file `~/.accademia-arcana/salvataggio.properties` (nella cartella
dell'utente); per ricominciare da zero basta scegliere "Nuova partita" o cancellare quel file.

## 🎮 Come si gioca (in breve)

1. **Nuova partita**: scegli il nome e l'affinità (Piromante, Idromante, Geomante, Aeromante).
2. Ogni anno ha **5 settimane**; in ogni settimana scegli **una** attività: allenamento,
   lezione di rune, lezione di elementi oppure un duello contro un avversario.
3. Gli **incantesimi** nascono combinando ogni elemento conosciuto con ogni runa conosciuta.
4. Nei **duelli a turni** conta la ruota degli elementi (▲ Fuoco batte △ Aria, △ Aria batte
   ▽ Terra, ▽ Terra batte ▼ Acqua, ▼ Acqua batte ▲ Fuoco).
5. A fine anno affronti il **guardiano**: se vinci passi all'anno successivo, se perdi ripeti l'anno.
   Dopo il quinto guardiano ti diplomi.

Nel gioco il pulsante **Guida** spiega tutte le regole e i numeri del bilanciamento.

## 🤖 Uso di strumenti di AI

Durante tutto il progetto è stato utilizzato **Claude (Anthropic)** come tutor e assistente di
programmazione. L'uso è stato:

* Utilizzato Claude per:
   * la realizzazione della grafica sia per migliorare l'organizzazione del codice
   * la grafica, che mi ha supportato nella creazione e nell'organizzazione dell'interfaccia grafica utilizzando JavaFX, la libreria Java dedicata allo sviluppo di interfacce grafiche
   * risolvere problemi di configurazione (Gradle, IntelliJ, Git e GitHub)
   * bilanciare i numeri del gioco con simulazioni di partite
   * chiedere consigli e suggerimenti per individuare soluzioni migliori, rendere il codice più ordinato, leggibile e ben strutturato.



