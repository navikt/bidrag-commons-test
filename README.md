# bidrag-commons-test

 ![](https://github.com/navikt/bidrag-commons-test/workflows/continious%20integration/badge.svg)

Komponenter for testing som brukes på tvers av applikasjoner under bidrag

## continuous integration and deployment

Gjøres med 'workflows' og 'actions' fra GitHub. Se `.github/workflows/*` for detaljer. 

## release endringer

versjon | endringstype | beskrivelse
--------|--------------|-------------
0.2.0   | -- ingen --  | Overgang til maven repo fra github 
0.1.0   | slettet      | `SecuredTestRestTemplate`: Fjernet avhengighet til spring-oidc-support fra nav 
0.0.12  | endring      | `HttpHeaderTestRestTemplate.postForEntity(...)`
0.0.12  | endring      | `HttpHeaderTestRestTemplate`: slettet konstruktør (kun brukt av `SecuredTestRestTemplate`) 
0.0.11  | endring      | ny java baseline -> java 12
0.0.10  | endring      | Adding custom headers only valid for one HttpEntityCallback 
0.0.9   | endring      | `HttpHeaderTestRestTemplate` med alternativ metode for å legge på en custom header
0.0.6   | opprettet    | `HttpHeaderTestRestTemplate` som kan konfigureres og som arves av `SecuredTestRestTemplate`
0.0.4   | endring      | `SecuredTestRestTemplate` med public constructor
0.0.1   | opprettet    | `SecuredTestRestTemplate`
