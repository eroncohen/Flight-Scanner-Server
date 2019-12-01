package smartspace;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import smartspace.dao.ActionDao;
import smartspace.dao.memory.MemoryElementDao;
import smartspace.data.ActionEntity;
import smartspace.data.ElementEntity;
import smartspace.data.Location;
import smartspace.data.util.EntityFactory;
//@Profile("production")
//@Component
public class ActionEntityDemo implements CommandLineRunner {

	private EntityFactory factory;
	private ActionDao actionDao;
	int a;
	
	
	@Autowired
	 public ActionEntityDemo(EntityFactory factory,ActionDao actionDao) {
	 this.actionDao=actionDao;
	 this.factory=factory;
	}
	
	@Override
	public void run(String... args) throws Exception {
		
		System.out.println("\n\n ----------------  Action Entity Start: -------------------- \n");
		String name = "element";
		String type = "filght";
		Location location = new Location(1, 5);
		Date creationTimeStamp = new Date();
		String creatorEmail = "Sagiv.asraf@s.afeka.ac.il";
		String creatorSmartspace = "Ricky";
		boolean expired = false;
		Map<String, Object> moreAttributes1 = null;

		ElementEntity element = this.factory.createNewElement(name, type, location, creationTimeStamp, creatorEmail,
				creatorSmartspace, expired, moreAttributes1);
		Map<String, Object> moreAttributes =new HashMap<String, Object> ();
		MemoryElementDao elementDao=new MemoryElementDao();
		element=elementDao.create(element);
		moreAttributes.put("creatoeId", "305256569");

		ActionEntity action = this.factory.createNewAction(element.getElementId(), element.getElementSmartspace(), 
				"Book", new Date(), element.getCreatorEmail(), element.getCreatorSmartspace(), moreAttributes);
		System.err.println("new action:\n" + action);
		action= this.actionDao.create(action);
		System.err.println("stored action:\n" + action);
		this.actionDao.deleteAll();
		if (this.actionDao.readAll().isEmpty()) {
			System.err.println("\nSuccessfully deleted all actions");
		
		} else {
			throw new RuntimeException("Error! there is an action in the memory after deletion");
		}
		System.out.println("\n ----------------  Action Entity End -------------------- \n\n");

	}

}
