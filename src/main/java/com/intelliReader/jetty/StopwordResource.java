package com.intelliReader.jetty;

import com.intelliReader.storage.BerkelyDBStore;
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
    public Entry getEntry( @PathParam("id") String id) {
        if(bdbStore.get(id) != null) {
            return new Entry(id, bdbStore.get(id).toString());
        }else{
            return null;
        }
    }

    @POST
    public Entry saveEntry(Entry entry) throws Exception{
        assert entry.getId() != null;
        Date d = Calendar.getInstance().getTime();
        bdbStore.put(entry.getId(),d);
        bdbStore.sync();
        return new Entry(entry.getId(),d.toString());
    }

    @DELETE
    @Path("{id}")
    public void deleteEntry(@PathParam("id") String id) throws Exception{
        bdbStore.delete(id);
        bdbStore.sync();
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
                                               Integer pageSize) throws Exception{
        PaginatedListWrapper<Entry> paginatedListWrapper = new PaginatedListWrapper<Entry>();
        paginatedListWrapper.setCurrentPage(page);
        paginatedListWrapper.setSortFields(sortFields);
        paginatedListWrapper.setSortDirections(sortDirections);
        paginatedListWrapper.setPageSize(pageSize);
        return findEntries(paginatedListWrapper);
    }

    private PaginatedListWrapper<Entry> findEntries(PaginatedListWrapper<Entry> wrapper) throws Exception {
        wrapper.setTotalResults(countEntries());
        int start = (wrapper.getCurrentPage() - 1) * wrapper.getPageSize();
        wrapper.setList(findEntries(start,
                wrapper.getPageSize(),
                wrapper.getSortFields(),
                wrapper.getSortDirections()));
        return wrapper;
    }

    @SuppressWarnings("unchecked")
    private List<Entry> findEntries(int startPosition, int maxResults, String sortFields, String sortDirections)
            throws Exception {
        List<Entry> entries = new ArrayList<Entry>();
        for(String k: bdbStore.getKeys()){
            entries.add(new Entry(k,bdbStore.get(k).toString()));
        }
        Collections.sort(entries, new Comparator<Entry>() {
            @Override
            public int compare(Entry o1, Entry o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });
        int endPosition = (startPosition + maxResults) <= countEntries() ? (startPosition + maxResults) : countEntries();
        return entries.subList(startPosition,endPosition);
    }


    private Integer countEntries() throws Exception {
        return bdbStore.getKeys().size();
    }

    public static BerkelyDBStore<String, Date> bdbStore =
            new BerkelyDBStore<String, Date>(System.getProperty("user.dir") + "/src/main/resources/iReader",
                    String.class, Date.class, "stopwordTable" ); ;
}


