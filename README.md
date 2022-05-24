# Gods Unchained Card Selector

## Technical Assessment

Exercise described [here](https://gist.github.com/maarcosd/321d8a67cc5c038c5b914e4e999a1b98) but for mobile.

## Overview
- Coded using Android Studio Android Studio Arctic Fox | 2020.3.1 Patch 2
- Tested on Google Pixel 5 Android version 11 only
- There is a demo video and release APK ready to be installed in the `demo` foldere

### Project structure
- `data`: Manages data for the app e.g. network calls, contains the repository and API models
- `di`: Dependency injection
- `domain`: Contains the core business logic/use cases, domain models
- `feature`: presents UI to the user, handles user interactions
- `ui.theme`: colours and theme for the app

### Components
- View (CardSelectorScreen): Renders UI using Jetpack Compose, contains a reference to the view model, passes user interaction to the view model via view events
- View Model (CardSelectorViewModel): Contains logic on how the view should display data through view state, decides how the view should react to user interaction, talks to the manager to retrieve the required data
- Manager: Makes API requests to the API service, processes data from the API, converts API models to domain models, contains the core business logic/use cases
- Repository: Initiates API requests to the server

### Unit tests
Given that all the business logic is in the view model and manager, unit tests are written for these two classes only.

## Assumptions/Notes
- Noticed that `https://api.godsunchained.com/v0/proto` supports pagination, but I did do it in the app since one of the requirement is to filter the list by name or effect, which is not supported by the API (based on [this]( https://github.com/immutable/gods-unchained-api)). If I were to do it I would have to fetch all 1193 items from the API then do a local search, but the app would be fetching a lot of data and the app might not be performant.
- Given this is a mobile app, I did not make the header (selected card and search field section) sticky as mobile screens are usually small and there might not be enough or any space for the user to scroll the cards list below. If this mobile app is strictly for tablets of a certain size, making the header sticky would be better UX-wise because the user should be able to see the card they selected above without having to scroll back up to the top.
- The mobile app is locked to landscape so the design looks more like the requirement