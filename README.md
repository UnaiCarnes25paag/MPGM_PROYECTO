Android Banku Proiektua

Deskribapena:
Aplikazio honek Android-erako sistema bankario bat simulatzen du, non erabiltzaileak bere diru-sarrerak eta gastuak modu errazean kudea ditzakeen. Oinarrizko eragiketak egiteko aukera ematen du, hala nola sarrerak, gastuak eta transakzioen laburpena kontsultatzea.

Funtzionalitate nagusiak

* Pantaila nagusia: Dirua sartzeko, ateratzeko eta kontuaren laburpena ikusteko aukeren sarbide azkarra.
* Transakzioen kudeaketa: Sarrera eta gastuen erregistroa kategoriaren arabera (adibidez: janaria, aisialdia, erosketak, garraioa, etxea).
* Kontuaren laburpena: Erregistratutako transakzio guztiak ikustea, zenbatekoa eta kategoria barne.

NFC eta Ordaindu Automatikoko integrazioa

Funtzionalitate aurreratuak gehitu dira, NFC (Host Card Emulation) bidezko ordainketa automatikoak simulatuz eta salmenta-puntuetako terminalekin (TPV) komunikatzeko:
GitHub-en MPGM_PROYECTO_TPV izeneko repositorio bat gehitu da, azken funtzionalitate hau probatzeko TPV baten funtzionalitatea simulatuz.

Ordaindu Automatikoko Modua:

* Modua aktibatzean, aplikazioa prest dago TPV batetik ordainketak jasotzeko.
* Ez da beharrezkoa merkataritza edo zenbatekoa eskuz sartzea; TPV-k datuak zuzenean bidaltzen ditu.
* Aplikazioak automatikoki erregistratzen du transakzioa datu-basera, bere kategoria eta motarekin (gastu edo sarrera).

HCE Zerbitzua (Host Card Emulation):

* TPV batetik jasotzen ditu datuak JSON formatuan (merkataritzaren izena, zenbatekoa eta mota).
* Informazioa prozesatzen du eta transakzioa automatikoki gordetzen du tokiko datu-basetan.
* Aplikaziotik azken ordainketa kontsultatzeko aukera ematen du informazio botoi baten bidez.

Erabilitako teknologiak:

* Kotlin aplikazioaren logika guztirako.
* Room Database transakzioen biltegirako.
* NFC HCE banku-txartela imitatzen eta TPV simulatu batetik ordainketak jasotzeko.
* Coroutines datu-baseko eragiketak atzeko planoan egiteko.
* XML Layout-ak gradiente eta botoi pertsonalizatuak dituzten interfaz erakargarri baterako.
