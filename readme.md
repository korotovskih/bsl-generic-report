# bsl-generic-report

Формирует [отчет](https://docs.sonarqube.org/latest/analysis/generic-test/) содержащий строки, являющиеся кодом

## example

```shell script
bsl-generic-report make src report.xml
```

## Usage

```shell script
Usage: make [-hV] <infile> <outFile>
make coverage report
      <infile>    каталог содержащий файлы *.bsl
      <outFile>   имя файла результата result.xml
  -h, --help      Show this help message and exit.
  -V, --version   Print version information and exit.
```

## build

```shell script
gradlew build
```
