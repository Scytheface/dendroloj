# dendroloj

## Paigaldamine (IntelliJ IDEA)
1. Lae alla uusim versioon [siit](https://github.com/Scytheface/dendroloj/releases/latest).
2. Ava IntelliJ-s projekt kus soovid dendroloj-d kasutada.
3. Ava File -> Project Structure -> Modules -> Dependencies.
4. Vajuta + märgile ning vali 'JARs or Directories...'.
5. Navigeeri alla laetud dendroloj .jar faili juurde ja vali see.
6. Vajuta 'OK'.

## Kasutamine
1. Lisa meetoditele mille rekursioonipuud tahad visualiseerida `@Grow` annotatsioon.
2. Enne oma rekursiivse meetodi välja kutsumist kutsu välja `Dendrologist.wakeUp()`.
3. Käivita programm ja aken rekursioonipuuga avaneb automaatselt.
