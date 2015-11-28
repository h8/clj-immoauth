# clj-immoauth

A simple Clojure application which uses [Immutant](http://immutant.org) as a web server and demonstrates user login via OAuth 2.0 (Google). Also it's an example of using code hot-reload with Immutant.

## Usage

* Get credentials for application on Google Developer Console and put Client Key and Client Secret into resources/config.edn. Also add http://localhost:8080/oauth to "Authorized redirect URIs" on Credentials page.
* Run application via `lein run`
* Go to http://localhost:8080/profile

Alternatively you can run application with code hot-reload option with Leiningen:
* `lein with-profile repl repl`
* `clj-immoauth.core=> (run-dev-server)`

or using [CIDER](https://github.com/clojure-emacs/cider) for Emacs (it will load repl profile automatically). Please note, there is no need to add anything to Leiningen profiles (~/.lein/profiles.clj). Also keep in mind, that if you change routing, server should be restarted.

## License

Dmitry Stropalov © 2015

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
