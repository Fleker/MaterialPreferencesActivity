# MaterialPreferencesActivity
A glue library for Material Design preferences

This library uses the following:

    compile 'com.github.fleker:settingsmanager:1.1.1'
    compile 'com.jenzz:materialpreference:1.3'
    compile('com.github.afollestad.material-dialogs:core:0.8.5.3@aar') {
        transitive = true
    }
    compile('com.github.afollestad.material-dialogs:commons:0.8.5.3@aar') {
        transitive = true
    }
    
To greatly simplify your work in designing and maintaining preferences.

You can use Gradle to import this library:

`compile 'com.felkertech.materialpreferencesactivity:0.1.2'`

## Get Started
To get started, create a new activity and extend `MaterialPreferencesActivity`. Then, override the methods and implement them based on your own app's specifications.
