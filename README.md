# Color quantizer

## Client commands

- whoami
- register <username> <password>
- login <username> <password>
- logout

- list-images
- upload <filename> <local-path>
- download <filename> <local-path>
- delete <filename>

- quantize <filename> <number-of-colors> <number-of-thread>

## Server

### Управление на потребители

Информацията ще се съхранява в текстов файл(не знам, дали се иска приложението да е сигурно и да поддържа неща като хеширане и salt)

### Управление на файловете(изображенията) за всеки потребител

Ще има една централна директория(нека я наречем "./files"). Всеки потребител ще има поддиректория в нея(примерно потребител deyandd има "./files/deyandd"), която ще работи като работна директория за този потребител. Ще поддържа извеждане на списък с изображенията в поддиректорията му, качване, сваляне и изтриване на изображенията.

### Алгоритъм за генериране на изображение

Алгоритъмът(Kmean++) ще генерира изображение с заден брой цветове, което максимално наподобява оригиналното. Резултата ще се запазва в работната папка на потребителя с име "<оригиналното име>-<броя на цветовете>.<оригиналното разширение>".

## Client
- register deyan pass
- login deyan pass
- upload landscape.jpg ./resources/client/landscape.jpg
- color-quantize landscape.jpg 10
- download landscape_k10.jpg ./resources/client/process_landscape.jpg