# bidrag-commons-test
Komponenter for testing som brukes på tvers av applikasjoner under bidrag

## release endringer

versjon | endringstype | beskrivelse
--------|--------------|-------------
0.0.11  | endring      | nytt java baseline -> java 12
0.0.10  | endring      | Adding custom headers only valid for one HttpEntityCallback 
0.0.9   | endring      | `HttpHeaderTestRestTemplate` med alternativ metode for å legge på en custom header
0.0.6   | opprettet    | `HttpHeaderTestRestTemplate` som kan konfigureres og som arves av `SecuredTestRestTemplate`
0.0.4   | endring      | `SecuredTestRestTemplate` med public constructor
0.0.1   | opprettet    | `SecuredTestRestTemplate`
