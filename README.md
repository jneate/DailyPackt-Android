# Packt Daily

A simple Android application for keeping tabs on Packt's daily free book giveaway.

[![Play Store Link](https://cdn4.iconfinder.com/data/icons/free-colorful-icons/360/google_play.png)](https://play.google.com/store/apps/details?id=com.iambenzo.dailypackt)

## Use Case

I used a Python program, which I contributed to, in order to collect my free book for me every time I turned on my PC. It was magical.

However, I'm not always with my computer to turn it on and run the program and also I don't always want what Packt has offered for the day.

I created this app in order to allow me to quickly view the free book of the day, before making a decision on whether I wanted it or not (without being pestered to buy something via full-page adverts).
 
## Features

* Persistent login 
* Reminder Notifications (Off by default)
* Keeps track of obtained books (via the app only, for now)
* Multi-format downloads (PDF, ePub, Mobi)

### Feature Pipeline

* Auto-grabber - for those who want all the books!
* Access to previously obtained books
* Open to suggestions/pull requests...

## Pull Request Friendly

Feel free to update the UI, add features (including those in the pipeline), or enhance the current codebase.

Send me a pull request and I'll review your work before adding it to the main program :)

## Libraries Used

[Jsoup](https://jsoup.org/) - For scraping the Packt web pages.

[Picasso](http://square.github.io/picasso/) - For asynchronous downloading, caching and displaying of images.

[SecuredPreferenceStore](https://github.com/iamMehedi/Secured-Preference-Store) - For safe storage of user credentials.

[GSON](https://github.com/google/gson) - For simple storage of collected books.

[EasyPermissions](https://github.com/googlesamples/easypermissions) - For simplified permission requests.