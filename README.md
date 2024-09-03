# PlayTime (fork of fork) 
A spigot plugin that records players' playtime stats.

**Thank you to the original developers F64_Rx, Twi5TeD, 852DuartePls + more; this is a fork of a fork.** It was created to fix some data errors I was experiencing with PlayTime v3.7. I forked it from 852DuartePls's release for 1.20.6 (for which at the time of creating this, the pull request has not been accepted), which is a fork of Twi5TeD's PlayTime repo. 

## Dependencies
- **Java 17+**: REQUIRED
- **Spigot 1.20.4+**: UNTESTED ON OLDER VERSIONS
- **PlaceholderAPI**: OPTIONAL

## Stream Changelog
- ***This* plugin:** https://github.com/tbm00/PlayTime

    v3.8.1
    - added file lock for asynchronous file writing tasks
    - refactored playerdata save calls

    v3.8, commit `be10c1fdb`
    - reverted to "legacy" config only (removed OlafHammer's multiserver support)
    - improved JSON handling and formatting
    - fixed some deprecated methods
- **Parent:** https://github.com/852DuartePls/PlayTime

    v3.7, commit `7c6f99740`
    - switched from 1.8.8 to 1.20.4 spigot api
    - fixed some deprecated methods
- **Grandparent:** https://github.com/Twi5TeD/PlayTime

    v3.7, commit `dc9b1c4bb`
    - minor changes, finalized version 3.7

- **Great-Grandparent:** https://github.com/Twi5TeD/PlayTime

    v3.6, commit `34747ed69`
    - added OlafHammer's multiserver support
