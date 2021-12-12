# Spotify Music Advisor
This is an educational project from JetBrains Academy named Music Advisor (https://hyperskill.org/projects/62?track=12)

CLI interface accept commands:
* auth -- starts authorization flow
* new -- new albums
* featured -- highlited albums
* categories -- list of categories' names
* playlists cat_name -- playlists corresponding to cat_name. cat_name is a category name from the list gnerated with categories command. cat_name is case sensetive.
* next -- next page of current command output
* prev -- previous page of current command output

## Example:
\> auth  
use this link to request the access code: https://accounts.spotify.com/authorize?client_id=f3f49f01499240a6bc05989e01ebc5cd&redirect_uri=http://localhost:8080&response_type=code  
waiting for code...  
Success!  
\> categories  
Top Lists  
Happy Holidays  
Pop  
Russian Rap  
Chill  
---PAGE 1 OF 4---  
Rock  
Gaming  
Focus  
Dance/Electronic  
Workout  
---PAGE 2 OF 4---  

