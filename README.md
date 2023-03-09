# Benedict 99-40
Benedict 99-40 is an extension of the JavaDestinyAPI project I started in August 2020. Its purpose is to facilitate easy access and management of my Destiny 2 clan, Heavenly Mayhem.

My Clan can be found at the following Bungie page: https://www.bungie.net/en/ClanV2?groupid=3074427

### Pre-Benedict Problems
Prior to the exisitence of this project, I was plagued with management issues of my Destiny 2 Clan. The most frustrating problem was syncing our users in-game with our users outside of the game. For about 2 years prior to the implementation of these systems, we had required for all of our members to be a part of our discord server, which was very difficult to enforce.
Up until I created the bot, I had to _manually_ compare lists of in-game members and members on discord. It was very frustrating to say the least, and after about ~7 months of not playing there was quite a bit of backlog of things I needed to check.

### Getting back into coding
I started coding in July 2015 when I attended a Java Summer camp. Java interested me because I knew it was used to create Minecraft, but that was about the extent of my knowledg at the time. I didn't pick up very much knowledge from that camp but I did get a limited understanding of how it worked.

In January 2020 my friend SJ contacted me about some problems with his Minecraft server. He and I had gained a lot of experience about Minecraft servers together by playing from 2015 - 2017. I was pretty impressed with what he had especially the small but strong community dedicated to playing that server every day. The purpose of the server was to be a place where students at a local high school could play during the school day.
I picked up Java again around this time and purchased a Bukkit Plugin Coding course on the learning platform Udemy on Feburary 6th, 2020. This opened a whole new perspective for me on the workings of programming languages and how they can be engineered to your advantage. I quickly began aquiring experience doing various coding jobs for my friend's server and quickly began looking at other oppurtunities to create stuff.

### Coronavirus and My First Understandings of the Bungie.net API
Coronavirus changed my daily life in that I no longer had to go to school. This provided me with a lot more free time than normal. I was able to utilize this new free time to hone my coding skills and investigate a lot of different things that I wouldn't have been able to previously. This includes Minecraft development and now discord bot development.
I was suprised to find that discord bots could be made in many different languages, including Java. I explored a lot of these frameworks and APIs throughout the earlier parts of the summer and eventually came across the Bungie.net API.

I was aware before this that there was an API provided by Bungie for third-party services. I had never bothered to look into this before because I had thought that it was limited to basic stats and item transfering. I was suprised to discover that it was very powerful and gave a lot of management abilities for Clan Leaders (Something that I was in dire need of).
I realized that I was now capable of doing several things:
- Tracking current members of the clan and linking their discord account to their in-game accounts
- Accessing several stats much more easily such as join dates
- Inviting, accepting requests to join and _kicking_ players

I was now able to automate a lot of the management of the clan that previously caused me headaches. Now it was just about implementing it in a meaningful way.

### JavaDestinyAPI (JDA)
The first step in this project was to create a dedicated library to send and interpret the responses of various endpoints of the Bungie.net API.
I spent about three weeks building a meaningful system that I could build my project upon. By the end I gained a wealth of knowledge relating to OAuth and REST systems that would help me on my way.
