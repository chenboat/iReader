package com.intelliReader.jetty;

import com.intelliReader.storage.MongoDBStore;
import com.intelliReader.storage.Store;
import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import java.util.*;


@Path("/stopwordList")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)

public class StopwordResource extends Application {

    @GET
    @Path("{id}")
    public Entry getEntry( @PathParam("id") String id,
                           @QueryParam("userId")String userId
                            ) throws Exception {
        String stored_id = userId + HTMLUtil.ACCOUNT_DELIMITER + id;
        if( store.get(stored_id) != null) {
            return new Entry(id,  store.get(stored_id).toString(), userId);
        }else{
            return null;
        }
    }

    @POST
    public Entry saveEntry(Entry entry) throws Exception{
        assert entry.getId() != null;
        Date d = Calendar.getInstance().getTime();
        store.put(entry.getUserId() + HTMLUtil.ACCOUNT_DELIMITER + entry.getId(), d);
        store.sync();
        return new Entry(entry.getId(),d.toString(),entry.getUserId());
    }

    @DELETE
    @Path("{id}")
    public void deleteEntry(@PathParam("id") String id,
                            @QueryParam("userId")String userId) throws Exception{
         store.delete(userId + HTMLUtil.ACCOUNT_DELIMITER + id);
         store.sync();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public PaginatedListWrapper<Entry> getAll(@DefaultValue("1")
                                               @QueryParam("page")
                                               Integer page,
                                               @DefaultValue("id")
                                               @QueryParam("sortFields")
                                               String sortFields,
                                               @DefaultValue("asc")
                                               @QueryParam("sortDirections")
                                               String sortDirections,
                                               @DefaultValue("50")
                                               @QueryParam("pageSize")
                                               Integer pageSize,
                                               @DefaultValue("anonymous")
                                               @QueryParam("userId")
                                               String userId
                                               ) throws Exception{
        PaginatedListWrapper<Entry> paginatedListWrapper = new PaginatedListWrapper<Entry>();
        paginatedListWrapper.setCurrentPage(page);
        paginatedListWrapper.setSortFields(sortFields);
        paginatedListWrapper.setSortDirections(sortDirections);
        paginatedListWrapper.setPageSize(pageSize);
        paginatedListWrapper.setUserId(userId);
        return findEntries(paginatedListWrapper);
    }

    private PaginatedListWrapper<Entry> findEntries(PaginatedListWrapper<Entry> wrapper) throws Exception {
        wrapper.setTotalResults(countEntries(wrapper.getUserId()));
        int start = (wrapper.getCurrentPage() - 1) * wrapper.getPageSize();
        wrapper.setList(findEntries(start,
                wrapper.getPageSize(),
                wrapper.getSortFields(),
                wrapper.getSortDirections(),
                wrapper.getUserId()));
        return wrapper;
    }

    @SuppressWarnings("unchecked")
    private List<Entry> findEntries(int startPosition, int maxResults,
                                    String sortFields, String sortDirections, String userId)
            throws Exception {
        List<Entry> entries = new ArrayList<Entry>();
        Map<String,Date> map = store.getAll();
        for(String k:  map.keySet()){
            if(k.startsWith(userId + HTMLUtil.ACCOUNT_DELIMITER)) {      // filter by member id
                entries.add(new Entry(k.substring((userId + HTMLUtil.ACCOUNT_DELIMITER).length()),
                            map.get(k).toString(),userId));
            }
        }
        Collections.sort(entries, new Comparator<Entry>() {
            @Override
            public int compare(Entry o1, Entry o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });
        int endPosition = (startPosition + maxResults) <= countEntries(userId) ?
                (startPosition + maxResults) : countEntries(userId);
        return entries.subList(startPosition,endPosition);
    }


    private Integer countEntries(String userId) throws Exception {
        int cnt = 0;
        for(String k:  store.getKeys()){
            if(k.startsWith(userId + HTMLUtil.ACCOUNT_DELIMITER)) {      // filter by member id
                cnt++;
            }
        }
        return  cnt;
    }

    public static Store<String, Date> store;

    static {
        try {
            store = new MongoDBStore<String, Date>("mongodb://heroku:heroku@ds029831.mongolab.com:29831/ireader_db",
                    "accountStopwords", "word", "time");
        }catch (Exception e){
            System.exit(1);     //TODO: log it somehow
        }
    }
}


