# owlet-status-ui

## Contributing

1. Clone the repo
2. `cd` into the cloned repo
3. Run `boot dev`
4. Navigate to `http://localhost:5000/`

## State Watch-ing

`; (add-watch app-state :logger #(-> %4 clj->js js/console.log))`
