"""Code for parsing the cleaned-up version of the Reuters-21578 corpus that
was distributed along with this code.

Use the read_reuters function to create a list of dict objects, each 
representing a single document from the corpus.

Use the get_labels function to produce a list of valid classification labels
from the list produced by read_reuters.

Sam Scott, Mohawk College, October 5, 2017"""
import xml.etree.ElementTree as et

def read_reuters(path="Reuters21578", limit=21578):
    ''' Reads the Reuters-21578 corpus from the given path. (This is assumed to 
    be the cleaned-up version of the corpus provided for this code.) The limit 
    parameter can be used to stop reading after a certain number of documents 
    have been read.'''
    
    def get_dtags(it, index):
        '''Helper function to parse the <D> elements'''
        dtags = []
        while it[index+1].tag == "D":
            dtags.append(it[index+1].text)
            index += 1
        return dtags, index

    docs = []
    numdocs = 0
    
    for i in range(22):
        pad = ""
        if i<10:
            pad = "0"
        print("Reading",path+'\\reut2-0'+pad+str(i)+'.sgm')
        
        tree = et.parse(path+'\\reut2-0'+pad+str(i)+'.sgm')
        root = tree.getroot()
        
        it = tree.getiterator()
        
        
        index = 0
        while index < len(it):
            if it[index].tag == "REUTERS":
                if numdocs == limit:
                    return docs
                docs.append({})
                numdocs+=1
            elif it[index].tag.lower() in ["topics", "places","people","orgs","exchanges","companies"]:
                docs[numdocs-1][it[index].tag.lower()], index = get_dtags(it, index)
            elif numdocs > 0:
                docs[numdocs-1][it[index].tag.lower()] = it[index].text
            
            index +=1
   
    return docs

def get_labels(docs, labeltype):
    ''' Returns a sorted list of labels from a list of documents that have 
    been parsed using read_reuters. The labeltype parameter can be "topics", 
    "people", "places", "exchanges", or "orgs".'''
    import operator
    
    labels = {}
    try:
        for doc in docs:
            for label in doc[labeltype]:
                labels[label] = 1 + labels.get(label,0)
    except:
        print("WARNING: '"+labeltype+"' not found.")
        
    return sorted(labels.items(), key=operator.itemgetter(1), reverse=True)
