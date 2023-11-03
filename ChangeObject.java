public class ChangeObject {

	int id = 0;
    String Location = "";
    String Add = "";
    String Remove = "";

    public ChangeObject(int id, String LocationString, String AddString, String RemoveString){
		this.id = id;
        Location = LocationString;
        Add = AddString;
        Remove = RemoveString;
    }
}
