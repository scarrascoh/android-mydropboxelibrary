My Dropbox e-Library
=========================
Last update: 2014-02-03

Simple Android App that use Dropbox as a library for e-books (e-Library)

TESTED IN:
- Samsung Galaxy S (JellyBean 4.1)
- AVD Galaxy S (GingerBread 2.3.3)
- AVD Nexus 4 (JellyBean 4.3)

TO DO IMPROVEMENTS/NEW FEATURES:
- Error checking of several parts of the App
- Check if the user is connected to Internet when the App is opened
- Download the list of ebooks (metadata: title, author, cover...) 
to the device (caché) in order to improve the performance and 
the loading time of the list. Synchronize the local list with Dropbox
in order to detect changes.
- Test performance and optimize algorithms if ti's posible
- Detect folders with ebooks and no list them

KNOWN BUGS:
- (BUILD_VERSION < JELLY_BEAN) When the list of ebooks is sorted by date, the 
icons are not well assigned
