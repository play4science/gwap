# RESTful XML Interface

## Artigo: Tag frequencies
`http://localhost:8080/artigo/seam/resource/rest/artigodata/tags`

Example: `http://www.artigo.org/artigo/seam/resource/rest/artigodata/tags?dataset=artemis&language=en&threshold=3`

Restricted to users with permission "artigo / rest-data"

Parameters:

* `dataset`: mandatory, e.g. `artemis` (source name)
* `language`: optional, e.g. `de` or `en`, if none is provided everything is shown
* `threshold`: optional, default is `2`, must be >= `2`
