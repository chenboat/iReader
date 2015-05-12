package com.intelliReader.jetty;

import com.intelliReader.storage.MongoDBConnections;
import com.intelliReader.storage.MongoDBStore;
import com.intelliReader.storage.Store;
import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import java.util.*;


/**
 * User: ting
 * Date: 5/10/2015
 * Time: 10:16 PM
 */

@Path("/readList")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)

public class ReadListResource {
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
                                              @DefaultValue("40")
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
        Map<String,Map> map = store.getAll();
        for(String k:  map.keySet()){
            if(k.startsWith(userId + HTMLUtil.ACCOUNT_DELIMITER)) {      // filter by member id
                Map values = map.get(k);
                Entry e = new Entry(k.substring((userId + HTMLUtil.ACCOUNT_DELIMITER).length()),
                        values.get("date").toString(),userId);
                e.setSection((String)values.get("section"));
                entries.add(e);
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

    public static Store<String,Map> store = MongoDBConnections.visitedFeedMsgTitleStore;
}
