package fr.max2.factinventory.data;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;

import fr.max2.factinventory.client.model.item.RecursiveOverrideModel;
import net.minecraftforge.client.model.generators.CustomLoaderBuilder;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public class RecursiveOverrideModelBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T>
{
	public static <T extends ModelBuilder<T>> RecursiveOverrideModelBuilder<T> begin(T parent, ExistingFileHelper existingFileHelper)
    {
        return new RecursiveOverrideModelBuilder<>(parent, existingFileHelper);
    }

    private T base;

    protected RecursiveOverrideModelBuilder(T parent, ExistingFileHelper existingFileHelper)
    {
        super(RecursiveOverrideModel.Loader.ID, parent, existingFileHelper);
    }

    public RecursiveOverrideModelBuilder<T> base(T modelBuilder)
    {
        Preconditions.checkNotNull(modelBuilder, "modelBuilder must not be null");
        base = modelBuilder;
        return this;
    }

    @Override
    public JsonObject toJson(JsonObject json)
    {
        json = super.toJson(json);

        if (base != null)
        {
            json.add("base", base.toJson());
        }

        return json;
    }
}
