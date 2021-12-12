# Spotify Music Advisor
This is an educational project named [Music Advisor](https://hyperskill.org/projects/62?track=12) from JetBrains Academy.  
The app is a CLI utility that uses [Spotify web API](https://developer.spotify.com/documentation/web-api/reference/#/).  
The Spotify API wrapper located in **spotifyapi** package and separated from CLI app logic, and can be reused.


Command line interface accept commands:  
<code>auth</code> -- starts authorization flow  
<code>new</code> -- new albums  
<code>featured</code> -- highlited albums  
<code>categories</code> -- list of categories' names  
<code>playlists cat_name</code> -- playlists for cat_name. <code>cat_name</code> is a category name from the list gnerated with categories command, case sensetive.  
<code>next</code> -- next page of current command output  
<code>prev</code> -- previous page of current command output  
<code>exit</code> -- terminates app  

## Example:

<code>auth</code>  
use this link to request the access code: https://accounts.spotify.com/authorize?client_id=f3f49f01499240a6bc05989e01ebc5cd&redirect_uri=http://localhost:8080&response_type=code  
waiting for code...  
Success!  
<code>categories</code>  
Top Lists  
Happy Holidays  
Pop  
Rap  
Chill  
---PAGE 1 OF 4---  
<code>next</code>  
Rock  
Gaming  
Focus  
Dance/Electronic  
Workout  
---PAGE 2 OF 4---  
<code>playlists Workout</code>  
Cardio  
https://open.spotify.com/playlist/37i9dQZF1DWSJHnPb1f0X3  
  
Rock Your Body  
https://open.spotify.com/playlist/37i9dQZF1DXbFRZSqP41al  
  
Beast Mode Hip-Hop  
https://open.spotify.com/playlist/37i9dQZF1DX9oh43oAzkyx  
  
Power Workout  
https://open.spotify.com/playlist/37i9dQZF1DWUVpAXiEPK8P  
  
Hype  
https://open.spotify.com/playlist/37i9dQZF1DX4eRPd9frC1m  
<code>exit</code>  
---GOODBYE!---  
