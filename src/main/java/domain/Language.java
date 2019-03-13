package domain;

public enum Language {
    EN("en"),
    ES("es");

    private final String language;

    Language(String language){
        this.language = language;
    }

    @Override
    public String toString(){
        return language;
    }
}
