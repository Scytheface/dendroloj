# _dendroloj_

## Paigaldamine (IntelliJ IDEA)
1. Lae alla uusim versioon [siit](https://github.com/Scytheface/dendroloj/releases/latest).
2. Ava IntelliJ-s projekt kus soovid _dendroloj_-d kasutada.
3. Kopeeri allalaetud .jar fail oma IntelliJ projekti kausta.
4. Paremklõpsa IntelliJ-s .jar failil ja vali 'Add as library'.  
   ![image](https://github.com/Scytheface/dendroloj/assets/5256211/c75a3b4f-92eb-4016-8801-5da6f2464378)
5. Vajuta avanenud aknas 'OK'.

## Kasutamine

### Rekursioonipuude visualiseerimine

1. Lisa meetoditele mille rekursioonipuud tahad visualiseerida `@Grow` annotatsioon.
2. Enne oma rekursiivse meetodi välja kutsumist kutsu välja `Dendrologist.wakeUp()`.
3. Käita programm ja aken rekursioonipuuga avaneb automaatselt.

Näide rekrusiivse Fibonacci arvude arvutamise meetodiga (vt. [Katsed.java](src/test/java/Katsed.java)):  
![image](https://github.com/Scytheface/dendroloj/assets/5256211/fe3ca679-a942-4e39-8611-f4c8536cdca4)

### Kahendpuude joonistamine

1. Loo oma puu ja salvesta selle juurtipp muutujasse (nt `tipp`).
2. Kutsu välja `Dendrologist.drawBinaryTree`. Meetod võtab argumendiks juurtipu ja kolm funktsiooni, mis võtavad kõik sisendiks tipu ja tagastavad vastavalt tipu tähise sõnena, vasaku alamtipu ja parema alamtipu.

   Nt. kui tipu tähis on väljas .info, ning vasak ja parem alamtipp vastavalt väljades .v ja .p, siis saab puu joonistada väljakutsega `Dendrologist.drawBinaryTree(tipp, t -> t.info, t -> t.v, t -> t.p)`.

(Kui tegemist ei ole kahendpuuga saab kasutada üldisemat meetodit `Dendrologist.drawTree`. Kasutuse kohta leiab lisainfot meetodi __Javadoc__ kommentaarist.)

Näide kasutaja defineeritud Tipp klassiga (vt. [TippKatsed.java](src/test/java/TippKatsed.java)):  
![image](https://github.com/Scytheface/dendroloj/assets/5256211/04b18c0b-4281-4c06-b3df-2b12a55b8867)

### Graafide joonistamine

Graafide joonistamiseks saab kasutada klassi `GraphCanvas<V>`, kus `V` on joonistatava graafi tippude tüüp.
See klass toimib lõuendina, kuhu saab vastavate meetodite abil joonistada graafi servad ja tipud.
Lõuendi kuvamiseks kutsu välja meetod `Dendrologist.drawGraph(lõuend)`.

Lisainfot leiab `GraphCanvas` klassi ja vastavate meetodite _Javadoc_ kommentaaridest.

Vaata ka näidet failis [GraafKatsed.java](src/test/java/GraafKatsed.java).

### Graafilise liidese kasutamine

Liigutades riba akna alumises servas saab vaadata väljakutsete puu ajalugu. Iga samm on kas üks meetodi väljakutse või üks tagastus meetodist.

Vasakut hiireklahvi all hoides saab märgistada tippe (see värvib tipud erksiniseks).

Hiire rullikuga saab suumida sisse ja välja. Paremat hiireklahvi all hoides saab liigutada vaadet.  
`Ctrl+R` või `Ctrl+0` taastab algse vaate asukoha ja suurenduse.

## Lisavõimalused

### Seadistamine

Soovi korral on võimalik seadistada mõningaid parameetreid, mis muudavad seda kuidas _dendroloj_ rekursioonipuid kuvab. Seadete muutmiseks on `Dendrologist` klassil klassimeetodid mida peaks kutsuma enne `Dendrologist.wakeUp()` meetodit.

* `Dendrologist.setUIScale(uiScale)` seab kordaja millega korrutatakse kõikide graafiliste elementide mõõtmed. See on kasulik ennekõike kui vaikesuurusega tekst on lugemiseks liiga väike.
* `Dendrologist.setShowMethodNames(showMethodNames)` lülitab sisse või välja meetodite nimede kuvamise rekursioonipuus (vaikimisi on see väljas). See on kasulik, et vältida segadust, kui rekursioonipuus on mitu erinevat meetodit.
* `Dendrologist.setArgumentCapture(duringCall, duringReturn)` võimaldab seadistada millal argumentide väärtused talletatakse. Vaikimisi talletatakse argumentide väärtused nii väljakutsel kui ka tagastusel. Väärtused tagastusel kuvatakse ainult siis, kui need erinevad väärtustest väljakutsel.

### Tippude värvimine

`@Grow` annotatsiooniga meetodites (ja meetodites mida kutsutakse `@Grow` annotatsiooniga meetoditest) saab kasutada `Dendrologist.paint(color)` meetodit, et värvida rekursioonipuus sellele väljakutsele vastav tipp antud värvi. See on kasulik näiteks selleks, et tähistada rekursioonipuus huvipakkuvaid tippe.

### Kasutamine koos siluriga (IntelliJ IDEA)

Kui tahad, et _dendroloj_ toimiks sel ajal kui silur programmi jooksutamise pausile on pannud siis paremklõpsa *breakpointil* ja vali 'Suspend: Thread' ja seejärel vajuta 'Make Default'. Kui sul on mitu *breakpointi* juba lisatud siis pead 'Suspend: Thread' valima kõigil, kuid uute *breakpointide* lisamisel seda enam käsitsi vahetama ei pea.

See tagab, et silur paneb pausile ainult vaadeldava lõime ja teised lõimed sh _dendroloj_ lõim jooksevad edasi.
