package grammaticalEvolution;

public class Codon {
    private char[] binaryValue;

    public Codon(char[] binaryValue){
        this.binaryValue = binaryValue;
    }
    public int getDenaryValue(){
        return Integer.parseInt(new String(binaryValue),2);
    }

    public void mutate(int index){
        binaryValue[index] = (char) (binaryValue[index] ^ 1);//flip the bit at the index using XOR operator
    }

    public Codon clone(){
        char[] val = new char[binaryValue.length];

        for(int i=0;i<val.length;i++){
            val[i] = binaryValue[i];
        }

        return new Codon(val);
    }
}
