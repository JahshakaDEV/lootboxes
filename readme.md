<h1>Lootbox System</h1>

<ul>
<li>/lootbox um Lootbox Menu zu sehen</li>
<li>Beispiel Config schon für Testzwecke im Plugin drin</li>
<h2>Items.yml Config</h2>
id: id <br>
name: Ingame name<br>
amount<br>
material as string<br>
probability: wenn ein item 1 hat und das andere 10 ist das ander 10 mal so wahrscheinlich <br>
lore: liste von strings pro zeile
enchantments <br>
comamnd - was passiert wenn gewonnen wird %name und %uuid als variable <br>
& --> § für Farben
<br>
<br>
<h2>lootboxes.yml</h2>
id: id <br>
name: ingame-name <br>
price <br>
material as string <br>
location: int - main page location<br>
inShop: im shop zeigen<br>
onMainPage: auf main page anzeigen<br>
items: die id's der items die rein sollen <br>

</ul>
