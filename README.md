# conj/talk

[![License][license]][license-url]

My talk from Clojure/Conj 2023! To run the demos:

```sh
bb clerk-watch
```

Then visit http://localhost:7777 if it doesn't open automatically. Visit, change
and save any of the demos in `notebooks` to render them in Clerk.

> If you find this work interesting, please consider sponsoring it via [Github
> Sponsors](https://github.com/sponsors/sritchie). Thank you!

## Demos

The demos all live at https://sritchie.github.io/clojure-conj-2023/. Here's a
full directory of direct links:

- [Dual Number Visualization](https://sritchie.github.io/clojure-conj-2023/notebooks/conj/talk.html)
- [Taylor Series](https://sritchie.github.io/clojure-conj-2023/notebooks/conj/taylor_series.html)
- [Phase Portrait of the Pendulum](https://sritchie.github.io/clojure-conj-2023/notebooks/conj/phase_portrait.html)
- [(p, q) torus knot](https://sritchie.github.io/clojure-conj-2023/notebooks/conj/pq_knot.html)
- [Quartic Well](https://sritchie.github.io/clojure-conj-2023/notebooks/conj/quartic_well.html)
- [Colin's torus geodesics](https://sritchie.github.io/clojure-conj-2023/notebooks/conj/toroid.html)
- [My fork with an animating bead](https://sritchie.github.io/clojure-conj-2023/notebooks/conj/toroid_sim.html)
- [Rule 30 with Clerk](https://sritchie.github.io/clojure-conj-2023/notebooks/conj/rule_30.html)

## Links

- [Emmy repository](https://emmy.mentat.org)
- [Emmy-Viewers](https://emmy-viewers.mentat.org)
- The [Road to Reality Newsletter](https://roadtoreality.substack.com/)
- [Road to Reality Discord Server](https://discord.gg/hsRBqGEeQ4)
- [Clerk](https://clerk.vision), the notebook rendering engine used by Emmy
- [In-progress executable version](https://github.com/sicmutils/fdg-book) of _Functional Differential Geometry_ ([book link][fdg-book-url])
- [In-progress executable version](https://github.com/sicmutils/sicm-book) of Sussman and Wisdom's _Structure and Interpretation of Classical Mechanics_ ([book link][sicm-book-url])
- [JSXGraph.cljs](https://jsxgraph.mentat.org) and [`jsxgraph/clerk` template](https://github.com/mentat-collective/JSXGraph.cljs/tree/main/resources/jsxgraph/clerk)
- [MathBox.cljs](https://mathbox.mentat.org) and [`mathbox/clerk` template](https://github.com/mentat-collective/MathBox.cljs/tree/main/resources/mathbox/clerk)
- [MathLive.cljs](https://mathlive.mentat.org) and [`mathlive/clerk` template](https://github.com/mentat-collective/MathLive.cljs/tree/main/resources/mathlive/clerk)
- [Mafs.cljs](https://mafs.mentat.org) and [`mafs/clerk` template](https://github.com/mentat-collective/Mafs.cljs/tree/main/resources/mafs/clerk)
- [Leva.cljs](https://leva.mentat.org) and [`leva/clerk` template](https://github.com/mentat-collective/Leva.cljs/tree/main/resources/leva/clerk)

## Dependencies

Install the following dependencies:

- [Clojure CLI tools](https://clojure.org/guides/install_clojure)
- [`babashka`](https://github.com/babashka/babashka#installation)

You'll also need `node` installed, preferably via
[`nvm`](https://github.com/nvm-sh/nvm#installing-and-updating).

Run the following command to see all of the [Babashka
Tasks](https://book.babashka.org/#tasks) declared in `bb.edn`:

```sh
bb tasks
```

## Choosing an Editor

Clerk is a notebook environment that requires you to choose your own text editor
to work with the source files that generate your notebooks.

Here are links to guides for the most popular editors and Clojure plugins::

- [Calva](https://calva.io/jack-in-guide/) for [Visual Studio Code](https://code.visualstudio.com/)
- [Cider](https://docs.cider.mx/cider/basics/up_and_running.html#launch-an-nrepl-server-from-emacs) for [Emacs](https://www.gnu.org/software/emacs/)
- [Cursive](https://cursive-ide.com/userguide/repl.html) for [Intellij IDEA](https://www.jetbrains.com/idea/download/#section=mac)
- [Clojure-Vim](https://github.com/clojure-vim/vim-jack-in) for [Vim](https://www.vim.org/) and [Neovim](https://neovim.io/)

> If this is your first time using Clojure, I recommend
> [Calva](https://calva.io/jack-in-guide/) for [Visual Studio
> Code](https://code.visualstudio.com/).

## Developing with Clerk

You can develop against Clerk using its file watcher, using manual calls to
`clerk/show!`, or with a combination of both.

### Via File-Watcher

The simplest way to interact with Clerk is with Clerk's [file watcher
mode](https://book.clerk.vision/#file-watcher).

Run the following command to run the `serve!` function in `dev/user.clj`:

```sh
bb clerk-watch
```

Clerk will watch for changes of any file in the `notebooks` directory. The
ClojureScript build running in the background will pick up any changes to any
file in the `src` directory.

Change this by changing the value under `:watch-paths` in `user/serve-defaults`,
or passing an override to `bb clerk-watch`:

```
bb clerk-watch :watch-paths '["different_directory"]'
```

This will start the Clerk server at http://localhost:7777 with a file
watcher that updates the page each time any file in the `src` directory changes.

### REPL-Based Development

Alternatively, follow your editor's instructions (see ["Choosing an
Editor"](#choosing-an-editor) above) to start a Clojure REPL, and then run
`(user/serve!)`.

To show or reload a particular notebook, call `nextjournal.clerk/show!` with the
file's path as argument. The [Book of Clerk](https://book.clerk.vision) has
[good instructions on how to configure your editor for
this](https://book.clerk.vision/#editor-integration).

You can try this without any editor support by starting a REPL from the command
line:

```sh
clj -A:nextjournal/clerk
```

Then start the server:

```clj
(serve!)
```

To show a file, pass it to `clerk/show!`:

```clj
(clerk/show! "notebooks/conj/talk.clj")
```

> **Note**
> These commands work because dev/user.clj requires `nextjournal.clerk` under a
> `clerk` alias, and defines a `serve!` function.

## Custom ClojureScript and JavaScript

All ClojureScript code you add to `src/conj/custom.cljs` is available
for use inside any [custom viewer code you
write](https://book.clerk.vision/#writing-viewers).

This is made possible by the code in `src/conj/sci_viewers.cljs`. If you
want to add more namespaces, follow the instructions in `sci_viewers.cljs` to
get them into Clerk's SCI environment.

That file also contains instructions on how to make JavaScript and NPM
dependencies available to your viewers.

## Static Builds

Once you're ready to share your work, run the following command to generate a
standalone static build of your project to the `public/build` directory:

```sh
bb build-static
```

Start a local webserver and view the static build with the following command:

```
bb serve
```

Or run both commands in sequence with:

```
bb publish-local
```

> By default, the static build will include every file in the `notebooks`
> directory. Change this by changing the `:paths` entry in `static-defaults`
> inside `dev/user.clj`.

### GitHub

If you push this project to GitHub, the project is configured to publish a
static build to [GitHub Pages](https://pages.github.com/) on each commit to the
`main` branch.

> Disable this by deleting the `.github/workflows/gh-pages.yml` file.

To host this project on GitHub:

- [Create a GitHub repository](https://github.com/new). Ideally the owner
  matches `conj` and the project name is `talk`.
- Run the following in this project's directory:

```sh
git init
git add .
git commit -m "first commit"
git branch -M main
git remote add origin git@github.com:conj/talk.git
git push -u origin main
```

Then visit https://github.com/conj/talk to see your site.

### GitHub Pages

If you've hosted your project on GitHub, run the following to manually deploy your site to GitHub Pages:

```
bb release-gh-pages
```

By default your site will live at https://conj.github.io/talk.

### Clerk Garden

Nextjournal runs a site called [Clerk Garden](https://github.clerk.garden/) that
can generate and host your static builds for you.

Once your project is hosted on GitHub, simply visit

https://github.clerk.garden/conj/talk

to build and visit your compiled static site.

> Note that this URL will not automatically update on pushes to GitHub! You'll
> need to visit the url with `?update=1` appended to force it to update.

### Custom Domain

If you're hosting your static site at a custom domain ([see these
instructions](https://docs.github.com/en/pages/configuring-a-custom-domain-for-your-github-pages-site/managing-a-custom-domain-for-your-github-pages-site)),
modify `static-defaults` in `dev/user.clj` to pass the URL via the `:cname` key.

## Linting with `clj-kondo`

This project is configured with a GitHub action to lint all files using
[`clj-kondo`](https://github.com/clj-kondo/clj-kondo).

> Disable this by deleting the `.github/workflows/kondo.yml` file.

To initialize linting, run the following command:

```
clj-kondo --copy-configs --dependencies --lint "$(clojure -A:nextjournal/clerk -Spath)"
```

and commit all generated files.

```
bb lint
```

## Presentations

The presentations themselves live in `presentations/org/*.html`. see
`presentations/README.md` for more detail on how to:

- build these `html` files from their associated `org` files
- generate slides by executing Clojure code
- Serve these presentations in presenter mode

## License

Copyright © 2023 Sam Ritchie

_EPLv1.0 is just the default for projects generated by `clj-new`: you are not_
_required to open source this project, nor are you required to use EPLv1.0!_
_Feel free to remove or change the `LICENSE` file and remove or update this_
_section of the `README.md` file!_

Distributed under the Eclipse Public License version 1.0.

[clerk-url]: https://clerk.vision
[emmy-viewers-url]: https://emmy-viewers.mentat.org
[fdg-book-url]: http://mitpress.mit.edu/books/functional-differential-geometry
[license]: https://img.shields.io/badge/License-EPL%201.0-green.svg
[license-url]: LICENSE
[sicm-book-url]: https://mitpress.mit.edu/books/structure-and-interpretation-classical-mechanics-second-edition
