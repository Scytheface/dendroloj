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

Näide rekrusiivse Fibonacci arvude arvutamise meetodiga:
![image](https://github.com/Scytheface/dendroloj/assets/5256211/fe3ca679-a942-4e39-8611-f4c8536cdca4)


### Seadistamine

Soovi korral on võimalik seadistada mõningaid parameetreid, mis muudavad seda kuidas dendroloj rekursioonipuid kuvab. Seadete muutmiseks on `Dendrologist` klassil klassimeetodid mida peaks kutsuma enne `Dendrologist.wakeUp()` meetodit.

* `Dendrologist.setUIScale(uiScale)` seab kordaja millega korrutatakse kõikide graafiliste elementide mõõtmed. See on kasulik ennekõike kui vaikesuurusega tekst on lugemiseks liiga väike.
* `Dendrologist.setShowMethodNames(showMethodNames)` lülitab sisse või välja meetodite nimede kuvamise rekursioonipuus (vaikimis on see sees). See on kasulik, et vähendada visuaalset müra, kui meetodi nimi on pikk või väljakutseid on palju.


### Kasutamine koos siluriga (IntelliJ IDEA)

Kui tahad, et dendroloj toimiks sel ajal kui silur programmi jooksutamise pausile on pannud siis paremklõpsa *breakpointil* ja vali 'Suspend: Thread' ja seejärel vajuta 'Make Default'. Kui sul on mitu *breakpointi* juba lisatud siis pead 'Suspend: Thread' valima kõigil, kuid uute *breakpointide* lisamisel seda enam käsitsi vahetama ei pea.

See tagab, et silur paneb pausile ainult vaadeldava lõime ja teised lõimed sh dendroloj lõim jooksevad edasi.
