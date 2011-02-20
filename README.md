# snorri-model

Track stocks, perform analysis and give trading advise using the snorri-model
from Henrik Oude Nijhuis.

## Run interactive
$ lein repl
(require '[appengine-magic.core :as ae])
(use '[snorri-model.core] :reload-all)
(ae/serve snorri-model-app)

## Run devserver
$ lein appengine-prepare
$ dev_appserver.sh war/

## Push to production
$ lein appengine-prepare
$ appcfg.sh update war/

## License

Copyright (C) 2011 Joris Bontje

Distributed under the Eclipse Public License, the same as Clojure.
