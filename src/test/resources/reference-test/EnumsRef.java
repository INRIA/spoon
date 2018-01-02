
public class EnumsRef {

	enum Colors { Red, Black, White };
	
	void func()
	{
		Colors x;
		x = Colors.Black;

		EnumJar.Colors y;
		y = EnumJar.Colors.Black;
	}
}
