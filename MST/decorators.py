

def message(func):
    def wrapper(*args, **kwargs):
        args[0].awaken()
        func(*args, **kwargs)
    
    return wrapper