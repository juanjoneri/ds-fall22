## Instructions:

install git and pip and venv
```
sudo apt-get install git
sudo apt install python3-pip
sudo apt install python3-venv
```

init venv (takes a few minutes)
```
python3 -m venv testing
source testing/bin/activate
python3 -m pip install -r requirements.txt
```


Run the code

- first parameter is the name of the dataset
- second parameter is the list of seeds (nodes initially awake)
- use `-v` for verbose output
- use `-d` to draw the output graph

```
python main.py moons-100 10 -v -d
```