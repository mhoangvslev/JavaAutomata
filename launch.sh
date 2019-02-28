rm -r ./build
cd src/
mkdir ../build
echo "Compiling source codes..."
find -name "*.java" > sources.tmp
cat sources.tmp
javac -d ../build @sources.tmp
rm sources.tmp

echo "Making .jar ..."
cd ../build

echo "Main-Class: javaautomata.JavaAutomataApp" > javaautomata/MANIFEST.MF;
find -name "*.class" > sources.tmp
cat sources.tmp
jar cmvf javaautomata/MANIFEST.MF JavaAutomata.jar @sources.tmp
rm sources.tmp

echo "Launching application..."
gnome-terminal --title="JavaAutomata" -- java -jar JavaAutomata.jar
