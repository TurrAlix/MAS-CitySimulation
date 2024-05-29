# MAS-CitySimulation
This is the roject for [Multi-Agent Systems](https://www.unibo.it/it/studiare/dottorati-master-specializzazioni-e-altra-formazione/insegnamenti/insegnamento/2023/446615) of at the Master of Artificial Intelligence in Bologna. 

## Abstract 
We're *Alice Turrini* and  *Maud Ravanas-Deshays*, and we aim to simulate the traffic flows in a city!

The main characters of our city are **cars**, that drive around in the streets, and **pedestrians** that walk within buildings. They will have to interact with each other in crossroads or
zebra crossings and they have to rely on their perception of the environment to achieve their goals. 

These agents will be *autonomous and intelligent, capable of decision-making* and interaction.

To implement it, we will use the [Jacamo](https://jacamo-lang.github.io/) development platform, where Jason is used as the reference agent programming language. For the environment, there is [Cartago](https://sourceforge.net/projects/cartago/), a framework that provides an infrastructure for creating, managing, and
interacting with artifacts.

## Enviroment
- Buildings
- Streets
- Garage

## Agents
- Cars
- Pedestrian
- Helicopter

## Run the simulation
1.  Clone the repository: 
        
        git clone https://github.com/yourusername/MAS-CitySimulation.git
        cd MAS-CitySimulation

2. Build the project with Gradle:
        
        ./gradlew build

3. Run the the simulator:

        ./gradlew run

This will open a visualization window and a log window to see all agents and the communication between them.

For more detailed information, please refer to the initial report attached in this repository. :wink:

