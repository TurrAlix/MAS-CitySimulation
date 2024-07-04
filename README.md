# MAS-CitySimulation
This is the project for [Multi-Agent Systems](https://www.unibo.it/it/studiare/dottorati-master-specializzazioni-e-altra-formazione/insegnamenti/insegnamento/2023/446615) of at the Master of Artificial Intelligence in Bologna. 

## Abstract 
We're *Alice Turrini* and  *Maud Ravanas-Deshays*, and we aim to simulate the traffic flows in a city!

The main characters of our city are **cars**, that drive around in the streets, and **pedestrians** that walk within buildings. They will have to interact with each other in crossroads or
zebra crossings and they have to rely on their perception of the environment to achieve their goals. 

These agents will be *autonomous and intelligent, capable of decision-making and interaction*.

To implement it, we will use the [Jacamo](https://jacamo-lang.github.io/) development platform, where Jason is used as the reference agent programming language. For the environment, there is [Cartago](https://sourceforge.net/projects/cartago/), a framework that provides an infrastructure for creating, managing, and
interacting with artifacts.

## Enviroment
- **Buildings**: place where pedestrians can freely walk because cars are not allowed inside. There are four specific buildings, which are targets of some agents: 
    - a supermarket
    - a school
    - a park 
    - an office
Some buildings are disconnected from each other and the only possible connection
for pedestrians is through *zebra crossings* to safely cross the roads.
- **Streets**:  the location of car agents; indeed pedestrians are not
allowed to walk on them with the only exception of the zebra crossing blocks.
Each street lane has an assigned _direction_ (or two in case of crossroads) and some
of them have also _precedence_. Cars will have to take all these information into
account when moving to follow the traffic regulations.
- **Zebra Crossings**: specific street blocks, that allowes pedestrians to cross lanes to reach a different building. To avoid regrettable accidents, cars always check that there is not a pedestrian on these blocks before driving on them.
- **Garage**: This is the parking spot of the _helicopter_, an agent with unique characteristics that repairs broken cars.

## Agents
- **Cars**: They drive autonomously around the city, they need to follow the associated direction, and in *crossroads* they follow the constraint of precedence between different lanes. 
Two cars cannot share the same position as it would be similar to an accident.
If they come near an *obstacle* they will randomly choose another direction to take, but they can't go back. 
In *zebra crossing*, as assumed in real life they have to wait for pedestrians to have cleared the way before moving on. 
Sometimes cars *breaks down*, and in that case, they will stop moving until they calls for help and gets fixed by the repair helicopter.
- **Pedestrian**: They walk inside *buildings* and between them using *zebra crossings*. Contrary to cars, multiple pedestrian agents can share the same location. Their goal is to live their life doing some specific tasks going towards specific buildings, that change with the agent’s type:
    - *adult pedestrians* will first go to the office, and then to do the grocery shop
at the supermarket;
    - *child pedestrians* instead will first go to the school, and then to play at the
park.
Once they complete their path, they will stop moving because they finish their day!

- **Helicopter**: It doesn’t have to follow any kind of traffic regulations nor avoid buildings because it can fly. It will start parked at the garage and will go to a broken down car as soon as it
gets its SOS message. If another car calls for its help while it is going back to its garage, it interrupts its way back to directly go repair the other car.

## Structure of files
![File Structure](.\FileStructure.png)



## Run the simulation
We used Gradle as a build automation system for JVM-based application, in order to have 
dependencies management, compilation and easy launch by command line for this project.

1.  Clone the repository from Github: 
```bash
git clone https://github.com/yourusername/MAS-CitySimulation.git

cd MAS-CitySimulation
```

or clone the repository from GitLab (you need access for it)
```bash
git clone https://dvcs.apice.unibo.it/pika-lab/courses/mas/projects/mas-project-turrinideshays2324.git 

cd MAS-CitySimulation
``` 

2. Build the project with Gradle:
```bash
./gradlew build 
``` 

3. Run the simulator:
```bash
./gradlew run 
```

This will open a visualization window and a log window to see all agents and the communication between them.

## Changing worlds
In the `city_simulation.jcm` file there is the overview of our multi-agent system, with all the agents playing a role and the enrivoment selected. \
Going inside the workspace city, there are **4 worlds** we have created with different features and different agents. 

- **World 1**: There are 4 big blocks of building separated by a street with 2 lanes that creates a crossroads in the middle. Zebra crossing, the 4 different types of buildings and the helicopter are present in the environment. The agents playing a role here are 2 cars, one child, one adult and the helicopter.

- **World 2**: The simplest world to better test the interaction between cars and pedestrians on the zebra crossings. Indeed it is just a horizontal street with 2 lanes that divides the building blocks in an upper and lower side. In this case there is no helicopter but there are 4 cars and 4 pedestrians, to increase the possibilities of interactions

- **World 3**: It's very similar to the World 1 but it also have a street with 2 lanes all around the city that surrounds all the buildings. Zebra crossing, the 4 different types of buildings, and the helicopter are present in the environment. The agents involved here are 4 cars, 2 pedestrians, and the helicopter.

- **World 4**: Same as World 3 but with two more pedestrian to better analyse the communication between them.

To choose one of them it is just needed to decommenting that one and then run again the program! 


## Creating a new World
It's also possible to create whatever world you want! \
Here there are some few steps that are needed in order to create a proper functioning world:

> **Important**: We recommends to read all the other information present in the `city_simulation.jcm` for some more details and naming conventions used. 

\
1\) Go into **WorldModel.java** and *create a new world*: 
```java
static WorldModel worldX(){
        ...
}
```

> **Remember**: \
The number of agents as parameter of `WorldModelcreate()`
\
Change the name of the "*Scenario X*"
\
The *agId* must correspond to the id of the `city_simulation.jcm`

\
2\) Go into **city.java**:
- at the very beginning of the file at *simId* must be added 1 new word
- in the function `initWorld(int w)`, look inside the switch and add a new case: 
```java
case X: model = WorldModel.worldX(); break;
```

\
3\). Go into the **city_simulation.jcm**:

*Add agents* if necessary, as:
```java
agent agentName : fileName.asl  {
 	focus: city.viewN
}
```

And *change the first argument* of artifact 
```java
viewN: city.City(X, _)
```	

4. Run again the program to see your new World! 
```java
./gradlew run
```	

	
## Conclusion
For more detailed information, please refer to the final report attached in this repository. :wink:

## Authors
Alice Turrini: alice.turrini@studio.unibo.it

Maud Ravanas-Deshays: maud.ravanasdeshays@studio.unibo.it

