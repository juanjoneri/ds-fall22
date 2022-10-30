

class Fragment:
    def __init__(self, level: int, core: float=None):
        self.level = level
        self.core = core # weight of core edge (aka identity)
        
    def __eq__(self, other):
        return self.level == other.level and self.core == other.core
    
    def __hash__(self):
        return hash((self.level, self.core))
        