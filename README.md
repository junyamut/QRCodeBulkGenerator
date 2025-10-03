# QR Code Bulk Generator

#### (Spring Boot application)

*Description: Generates QR codes in bulk from a provided list.*

#### Notes
* Place list in Excel (.xlsx) spreadsheet. 
* Assume first row is always column header.
* Each row is one QR code. If row has multiple cells, it will be concatenated with \n as separator.
* Spreadsheet location and QR code image destination directories are all pre-defined in configuration file.
* Message dialog pop-up on error/successful completion of application.

#### TODO
* ~~Should place max number entries read from list.~~